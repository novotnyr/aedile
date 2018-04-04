package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.ImporterConfigurationException;
import com.github.novotnyr.aedile.git.ConfigurationImportException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 * Handles import of property files from a single specific directory.
 * <p>
 *     The importer takes a directory of <code>.properties</code>
 *     files. Each file represents a bottom-level directory in Consul K/V
 *     store. The properties in this file will be imported as a Consul keys and values.
 * </p>
 * <p>
 *     Suppose the following structure:
 *     <pre>
 *         env
 *           |-api
 *               |-application.properties
 *           |-impl
 *               |-application.properties
 *     </pre>
 *     <p>
 *         Suppose the default prefix, <code>config</code>.
 *     </p>
 *     <p>
 *         The import from the <code>/env/api</code> folder will
 *         create the folder <code>config/application</code> with
 *         keys and values taken from the <code>application.properties.</code>
 *     </p>
 * </p>
 */
public class PropertyFilesDirectoryImporter {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Default configuration prefix that maps to the Consul K/V.
     */
    public static final String DEFAULT_CONFIGURATION_PREFIX = "config";

    private ConsulConfigurationRepository configurationRepository;

    private String prefix = DEFAULT_CONFIGURATION_PREFIX;

    private boolean importSubdirectories = true;

    /**
     * Create an importer with a specific Consul configuration
     * @param configurationRepository Consul configuration holder
     */
    public PropertyFilesDirectoryImporter(ConsulConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Runs the import of the properties from a specified directory.
     *
     * @param configurationDirectory
     */
    public void run(File configurationDirectory) throws ImporterConfigurationException {
        if(!configurationDirectory.isDirectory()) {
            throw new ImporterConfigurationException("Directory "
                    + configurationDirectory
                    + " does not exist or is not a directory");
        }

        FileFilter filter = file -> file.getName().endsWith(".properties");
        File[] propertyFiles = configurationDirectory.listFiles(filter);
        if(propertyFiles == null || propertyFiles.length == 0) {
            logger.warn("No files found in {}", configurationDirectory);
        }

        importFiles(propertyFiles);
        importSubdirectories(configurationDirectory);
    }

    private void importFiles(File[] propertyFiles) {
        for (File propertyFile : propertyFiles) {
            logger.info("Handling properties in {}", propertyFile);
            Map<String, String> properties = loadProperties(propertyFile);
            configurationRepository.store(prefix, getConfigurationName(propertyFile), properties);
        }
    }

    private void importSubdirectories(File configurationDirectory) {
        if (!this.importSubdirectories) {
            return;
        }
        File[] subdirectories = configurationDirectory.listFiles(File::isDirectory);
        for (File subdirectory : subdirectories) {
            PropertyFilesDirectoryImporter directoryImporter = new PropertyFilesDirectoryImporter(this.configurationRepository);
            directoryImporter.setKeyPrefix(this.prefix + "/" + subdirectory.getName());
            directoryImporter.setImportSubdirectories(this.importSubdirectories);
            directoryImporter.run(subdirectory);
        }
    }

    /**
     * Extracts the name of the configuration from file name.
     * By default, the directory and the extensions is stripped. Thus, <code>/cfg/avatar.properties</code>
     * leads to configuration <code>avatar</code>.
     */
    private String getConfigurationName(File configurationFile) {
        return FilenameUtils.removeExtension(configurationFile.getName().toString());
    }

    /**
     * Loads the properties as a map from the specified {@link Properties} file
     * @param propertyFile a file with properties
     * @return a map with keys and values from the files
     * @see Properties#load(Reader)
     */
    private Map<String, String> loadProperties(File propertyFile) {
        try(BufferedReader reader = new BufferedReader(new FileReader(propertyFile))) {
            Properties properties = new Properties();
            properties.load(reader);
            return (Map) properties;
        } catch (IOException e) {
            throw new ConfigurationImportException("Unable to read configuration properties from " + propertyFile, e);
        }
    }

    /**
     * Set prefix for K/V key names.
     */
    public void setKeyPrefix(String keyPrefix) {
        this.prefix = keyPrefix;
    }

    /**
     * Configure whether to recursively import subdirectories.
     */
    public void setImportSubdirectories(boolean importSubdirectories) {
        this.importSubdirectories = importSubdirectories;
    }
}
