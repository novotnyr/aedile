package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;

import java.io.File;

public class FilesystemExporter {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Missing target directory");
            System.exit(1);
        }
        File targetDirectory = new File(args[0]);

        ConsulConfigurationRepository repository = newConsulConfigurationRepository();
        repository.export(targetDirectory);
    }

    private static ConsulConfigurationRepository newConsulConfigurationRepository() {
        ConsulConfiguration configuration = ConsulConfiguration.fromEnvironment();
        return new ConsulConfigurationRepository(configuration);
    }

}
