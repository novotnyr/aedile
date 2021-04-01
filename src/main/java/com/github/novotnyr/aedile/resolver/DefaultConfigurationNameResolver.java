package com.github.novotnyr.aedile.resolver;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class DefaultConfigurationNameResolver implements ConfigurationNameResolver {
    /**
     * Extracts the name of the configuration from file name.
     * By default, the directory and the extensions is stripped. Thus, <code>/cfg/avatar.properties</code>
     * leads to configuration <code>avatar</code>.
     */
    @Override
    public String getConfigurationName(File configurationFile) {
        return FilenameUtils.removeExtension(configurationFile.getName());
    }
}
