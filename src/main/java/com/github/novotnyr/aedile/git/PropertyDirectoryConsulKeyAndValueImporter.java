package com.github.novotnyr.aedile.git;

import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.ImporterConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertyDirectoryConsulKeyAndValueImporter {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_CONFIGURATION_PREFIX = "config";

    private ConsulConfigurationRepository configurationRepository;

    private String prefix = DEFAULT_CONFIGURATION_PREFIX;

    public PropertyDirectoryConsulKeyAndValueImporter(ConsulConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public void run(File configurationDirectory) {
        if(!configurationDirectory.isDirectory()) {
            throw new ImporterConfigurationException("Directory "
                    + configurationDirectory
                    + " does not exist or is not a directory");
        }

        FileFilter filter = file -> file.getName().endsWith(".properties");
        File[] propertyFiles = configurationDirectory.listFiles(filter);
        for (File propertyFile : propertyFiles) {
            logger.info("Handling properties in {}", propertyFile);
            Map<String, String> properties = loadProperties(propertyFile);
            configurationRepository.store(prefix, getConfigurationName(propertyFile), properties);
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
}
