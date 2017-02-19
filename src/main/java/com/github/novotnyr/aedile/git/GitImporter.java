package com.github.novotnyr.aedile.git;

import com.github.novotnyr.aedile.ConfigurationParser;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.filesystem.PropertyFilesDirectoryImporter;
import com.github.novotnyr.aedile.git.command.GitClone;
import com.github.novotnyr.aedile.git.command.GitPull;

import java.io.File;

/**
 * Imports key-value pairs into Consul K/V according to <code>git2consul</code>
 * configuration file.
 */
public class GitImporter {
    public void run(File configurationFile) {
        ConfigurationParser configurationParser = new ConfigurationParser();
        Configuration configuration = configurationParser.parse(configurationFile);

        Configuration.Repo repo = configuration.getRepos().get(0);
        String remoteUrl = repo.getUrl();
        String clonedRepositoryDirectory = configuration.getLocalStore();
        String configurationSubDirectory = clonedRepositoryDirectory + "/" + repo.getSourceRoot();

        String keyPrefix = getKeyPrefix(repo);
        if(keyPrefix == null) {
            keyPrefix = PropertyFilesDirectoryImporter.DEFAULT_CONFIGURATION_PREFIX;
        }

        if(isEmpty(clonedRepositoryDirectory)) {
            gitClone(remoteUrl, new File(clonedRepositoryDirectory));
        } else {
            gitPull(new File(clonedRepositoryDirectory));
        }

        ConsulConfigurationRepository repository = newConsulConfigurationRepository();
        PropertyFilesDirectoryImporter directoryImporter = new PropertyFilesDirectoryImporter(repository);
        directoryImporter.setKeyPrefix(keyPrefix);
        directoryImporter.run(new File(configurationSubDirectory));
    }


    /**
     * When building the key name,
     * concatenate mountpoint, repo name, branch name (assuming include_branch_name is true), and
     * the path of the file in your git repo.
     * Note: mountpoints can neither begin or end in with the character '/'. Such repo configs will be rejected.
     * @param repo repo configuration
     * @return concatenated key prefix
     */
    private String getKeyPrefix(Configuration.Repo repo) {
        String mountPoint = repo.getMountPoint();
        mountPoint = mountPoint != null ? mountPoint + "/" : "";

        return mountPoint + repo.getName();
    }

    protected void gitClone(String remoteUrl, File clonedRepositoryDirectory) {
        GitClone gitImporter = new GitClone();
        gitImporter.setRemoteUrl(remoteUrl);
        gitImporter.setClonedRepositoryDirectory(clonedRepositoryDirectory);
        gitImporter.run();
    }

    protected void gitPull(File repositoryDirectory) {
        GitPull gitPull = new GitPull();
        gitPull.setRepositoryFile(repositoryDirectory);
        gitPull.run();
    }

    private ConsulConfigurationRepository newConsulConfigurationRepository() {
        ConsulConfiguration configuration = ConsulConfiguration.fromEnvironment();
        return new ConsulConfigurationRepository(configuration);
    }


    private boolean isEmpty(String directoryPath) {
        File file = new File(directoryPath);
        if(!file.isDirectory()) {
            throw new ConfigurationImportException("The path does not denote a directory: " + directoryPath);
        }

        File[] files = file.listFiles();
        return files == null || files.length == 0;
    }

}
