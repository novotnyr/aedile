package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.git.PropertyDirectoryConsulKeyAndValueImporter;

import java.io.File;

public class FilesystemImporter {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Missing folder");
            System.err.println();
            System.exit(1);
        }

        File configurationSubDirectory = new File(args[0]);

        ConsulConfiguration consulConfiguration = ConsulConfiguration.fromEnvironment();

        ConsulConfigurationRepository repository = new ConsulConfigurationRepository(consulConfiguration);
        PropertyDirectoryConsulKeyAndValueImporter directoryImporter = new PropertyDirectoryConsulKeyAndValueImporter(repository);
        directoryImporter.setKeyPrefix(consulConfiguration.getPrefix());

        directoryImporter.run(configurationSubDirectory);
    }
}
