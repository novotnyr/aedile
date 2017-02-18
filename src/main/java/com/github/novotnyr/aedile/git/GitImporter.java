package com.github.novotnyr.aedile.git;

import com.github.novotnyr.aedile.ConfigurationParser;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.git.command.GitClone;
import com.github.novotnyr.aedile.git.command.GitPull;

import java.io.File;
import java.io.IOException;

/**
 * Imports key-value pairs into Consul K/V according to <code>git2consul</code>
 * configuration file.
 */
public class GitImporter {
    public void run(File configurationFile) throws IOException {
        ConfigurationParser configurationParser = new ConfigurationParser();
        Configuration configuration = configurationParser.parse(configurationFile);

        Configuration.Repo repo = configuration.getRepos().get(0);
        String remoteUrl = repo.getUrl();
        String clonedRepositoryDirectory = configuration.getLocalStore();
        String configurationSubDirectory = clonedRepositoryDirectory + "/" + repo.getSourceRoot();

        String keyPrefix = repo.getMountPoint();
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


    private boolean isEmpty(String directoryPath) throws IOException {
        File file = new File(directoryPath);
        if(!file.isDirectory()) {
            throw new IOException("The path does not denote a directory: " + directoryPath);
        }

        File[] files = file.listFiles();
        return files == null || files.length == 0;
    }

    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.err.println("Missing configuration file");
            System.exit(1);
        }

        String configurationFile = args[0];

        GitImporter importer = new GitImporter();
        importer.run(new File(configurationFile));
    }
}
