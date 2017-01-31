package com.github.novotnyr.aedile.git;

import com.github.novotnyr.aedile.ConfigurationParser;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.git.command.GitClone;
import com.github.novotnyr.aedile.git.command.GitPull;

import java.io.File;

/**
 * Imports key-value pairs into Consul K/V according to <code>git2consul</code>
 * configuration file.
 */
public class GitImporter {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Missing configuration file");
            System.exit(1);
        }

        String configurationFile = args[0];

        ConfigurationParser configurationParser = new ConfigurationParser();
        Configuration configuration = configurationParser.parse(new File(configurationFile));

        Configuration.Repo repo = configuration.getRepos().get(0);
        String remoteUrl = repo.getUrl();
        String clonedRepositoryDirectory = configuration.getLocalStore();
        String configurationSubDirectory = clonedRepositoryDirectory + "/" + repo.getSourceRoot();
        String keyPrefix = repo.getMountPoint();
        if(keyPrefix == null) {
            keyPrefix = PropertyDirectoryConsulKeyAndValueImporter.DEFAULT_CONFIGURATION_PREFIX;
        }

        if(isNonEmpty(clonedRepositoryDirectory)) {
            GitPull gitPull = new GitPull();
            gitPull.setRepositoryFile(new File(clonedRepositoryDirectory));
            gitPull.run();
        } else {
            GitClone gitImporter = new GitClone();
            gitImporter.setRemoteUrl(remoteUrl);
            gitImporter.setClonedRepositoryDirectory(new File(clonedRepositoryDirectory));
            gitImporter.run();
        }

        ConsulConfigurationRepository repository = newConsulConfigurationRepository();
        PropertyDirectoryConsulKeyAndValueImporter directoryImporter = new PropertyDirectoryConsulKeyAndValueImporter(repository);
        directoryImporter.setKeyPrefix(keyPrefix);
        directoryImporter.run(new File(configurationSubDirectory));

    }

    private static ConsulConfigurationRepository newConsulConfigurationRepository() {
        ConsulConfiguration configuration = ConsulConfiguration.fromEnvironment();
        return new ConsulConfigurationRepository(configuration);
    }

    private static boolean isNonEmpty(String directoryPath) {
        File directory = new File(directoryPath);
        if(! directory.isDirectory()) {
            return false;
        }
        return directory.listFiles().length > 0;
    }
}
