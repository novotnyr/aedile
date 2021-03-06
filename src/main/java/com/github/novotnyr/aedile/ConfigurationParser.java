package com.github.novotnyr.aedile;

import com.github.novotnyr.aedile.git.Configuration;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parses the configuration from a file.
 */
public class ConfigurationParser {
    /**
     * Parse the configuration from the <code>git2consul</code>
     * file to the {@link Configuration} object
     * @param file a JSON file with the configuration
     * @return fully parsed configuration
     * @throws ImporterConfigurationException if the file is not found or is not readable.
     */
    public Configuration parse(File file) {
        try(FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setPrettyPrinting()
                    .create();

            Configuration configuration = gson.fromJson(reader, Configuration.class);
            return configuration;
        } catch (FileNotFoundException e) {
            throw new ImporterConfigurationException("Configuration file " + file + " not found");
        } catch (IOException e) {
            throw new ImporterConfigurationException("Cannot parse configuration file " + file);
        }
    }
}
