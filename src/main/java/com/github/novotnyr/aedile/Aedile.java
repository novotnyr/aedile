package com.github.novotnyr.aedile;

import com.github.novotnyr.aedile.cmdline.CommandLineConfiguration;
import com.github.novotnyr.aedile.cmdline.FilesystemExportCommand;
import com.github.novotnyr.aedile.cmdline.FilesystemImportCommand;
import com.github.novotnyr.aedile.cmdline.FilesystemImportDatacenterCommand;
import com.github.novotnyr.aedile.cmdline.GitImportConfiguration;
import com.github.novotnyr.aedile.cmdline.HelpCommandConfiguration;
import com.github.novotnyr.aedile.filesystem.DatacenterFilesystemImporter;
import com.github.novotnyr.aedile.filesystem.PropertyFilesDirectoryImporter;
import com.github.novotnyr.aedile.git.GitImporter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Aedile {
    public static final Logger logger = LoggerFactory.getLogger(Aedile.class);

    public static void main(String[] args) {
        CommandLineConfiguration commandLineConfiguration = new CommandLineConfiguration();
        CmdLineParser cmdLineParser = new CmdLineParser(commandLineConfiguration);
        try {
            cmdLineParser.parseArgument(args);
            Object command = commandLineConfiguration.getCommand();
            if(command instanceof HelpCommandConfiguration) {
                help((HelpCommandConfiguration) command, cmdLineParser);
            }

            if(command instanceof FilesystemImportCommand) {
                filesystemImport((FilesystemImportCommand) command);
            }
            if(command instanceof FilesystemImportDatacenterCommand) {
                filesystemImportDatacenter((FilesystemImportDatacenterCommand) command);
            }
            if(command instanceof FilesystemExportCommand) {
                filesystemExport((FilesystemExportCommand) command);
            }
            if(command instanceof GitImportConfiguration) {
                gitImport((GitImportConfiguration) command);
            }
        } catch (CmdLineException | ImporterConfigurationException e) {
            System.err.println(e.getLocalizedMessage());
            System.err.println();

            System.err.println("Usage:\n");
            System.err.print("java -jar aedile.jar");
            cmdLineParser.printSingleLineUsage(System.err);

        }
    }

    private static void help(HelpCommandConfiguration command, CmdLineParser cmdLineParser) {
        cmdLineParser.printUsage(System.out);
    }

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
    private static void filesystemImport(FilesystemImportCommand commandConfiguration) {
        String configurationSubDirectoryString = commandConfiguration.getDirectory();
        if (configurationSubDirectoryString == null) {
            throw new ImporterConfigurationException("Import source directory must be set");
        }
        File configurationSubDirectory = new File(configurationSubDirectoryString);

        ConsulConfiguration consulConfiguration = ConsulConfiguration.fromEnvironment();
        describeToLog(consulConfiguration);

        ConsulConfigurationRepository repository = new ConsulConfigurationRepository(consulConfiguration);
        PropertyFilesDirectoryImporter directoryImporter = new PropertyFilesDirectoryImporter(repository);
        directoryImporter.setKeyPrefix(consulConfiguration.getPrefix());
        directoryImporter.setImportSubdirectories(! commandConfiguration.isRecursiveImportDisabled());

        directoryImporter.run(configurationSubDirectory);
    }

    private static void filesystemExport(FilesystemExportCommand commandConfiguration) {
        File targetDirectory = new File(commandConfiguration.getDirectory());

        ConsulConfiguration configuration = ConsulConfiguration.fromEnvironment();
        ConsulConfigurationRepository repository = new ConsulConfigurationRepository(configuration);

        repository.export(targetDirectory);
    }

    private static void filesystemImportDatacenter(FilesystemImportDatacenterCommand commandConfiguration) {
        File datacenterConfigurationSubDirectory = new File(commandConfiguration.getDirectory());

        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter();
        importer.configureExcludedFolders();

        importer.importDatacenters(datacenterConfigurationSubDirectory);
    }

    private static void gitImport(GitImportConfiguration gitImportConfiguration) {
        File configurationFile = gitImportConfiguration.getFile();

        GitImporter importer = new GitImporter();
        importer.run(configurationFile);
    }

    // Noncommand methods
    private static void describeToLog(ConsulConfiguration c) {
        if (! logger.isInfoEnabled()) {
            return;
        }
        StringBuilder msg = new StringBuilder("Consul configured: ");
        if (c.getUser() != null) {
            msg.append(c.getUser()).append("@");
        }
        msg.append(c.getHost())
                .append(":")
                .append(c.getPort());

        logger.info(msg.toString());
    }

}
