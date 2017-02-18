package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;

import java.io.File;

/**
 * Command-line client of the single directory import.
 *
 * <p>
 *     The client supports a single command-line argument, denoting the path to the directory
 *     that shall be imported to Consul.
 * </p>
 * <p>
 *     The Consul configuration is specified via environment variables.
 * </p>
 *
 * @see PropertyFilesDirectoryImporter
 */
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
        PropertyFilesDirectoryImporter directoryImporter = new PropertyFilesDirectoryImporter(repository);
        directoryImporter.setKeyPrefix(consulConfiguration.getPrefix());

        directoryImporter.run(configurationSubDirectory);
    }
}
