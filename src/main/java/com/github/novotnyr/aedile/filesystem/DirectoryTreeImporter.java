package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.git.ConfigurationImportException;
import com.github.novotnyr.aedile.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Imports a whole directory tree of arbitrarily nested directories.
 * <p>
 * The Consul key
 * prefix will be deduced from the relative path between <i>subdirectory</i>
 * and the <i>root directory</i>.
 * </p>
 * <p>
 *     Take the root directory <code>/etc/consul</code>. Take the
 *     subdirectory <code>/etc/consul/prod/application/config.properties</code>.     *
 * </p>
 * <p>
 *     The subdirectory will be imported with the prefix <code>prod/application</code>.
 * </p>
 * <p>
 *     The configurations in the <code>config.properties</code> will land in the
 *     <code>prod/application/config</code> directory.
 * </p>
 */
public class DirectoryTreeImporter {
    private ConsulConfigurationRepository configurationRepository;

    public DirectoryTreeImporter(ConsulConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Runs the import from the root directory.
     * @param rootDirectory a root directory that contains multiple subdirectories
     * @throws ConfigurationImportException when the import fails
     */
    public void run(File rootDirectory) throws ConfigurationImportException {
        if(! rootDirectory.isDirectory()) {
            throw new ConfigurationImportException("Supplied file is not a directory " + rootDirectory);
        }

        try {
            Files.walk(rootDirectory.toPath())
                    .filter(FileUtils::isFile)
                    .map(Path::getParent)
                    .map(Path::toFile)
                    .distinct()
                    .forEach(file -> importDirectory(rootDirectory, file));
        } catch (IOException e) {
            throw new ConfigurationImportException("Unable to import configuration wile traversing directory "
                    + rootDirectory, e);
        }
    }

    /**
     * Imports a subdirectory subdirectory of the root directory.
     * @param rootDirectory a directory that has been used to start the import
     * @param subdirectory a subdirectory of the root directory
     */
    protected void importDirectory(File rootDirectory, File subdirectory) {
        String keyPrefix = FileUtils.removeCommonPathPrefix(rootDirectory, subdirectory);

        ConsulConfigurationRepository repository = getConfigurationRepository();
        PropertyFilesDirectoryImporter directoryImporter = new PropertyFilesDirectoryImporter(repository);
        directoryImporter.setKeyPrefix(keyPrefix);

        directoryImporter.run(subdirectory);
    }

    /**
     * Return a configuration repository to handle the import
     * @return configuration repo
     */
    protected ConsulConfigurationRepository getConfigurationRepository() {
        return configurationRepository;
    }

}
