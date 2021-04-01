package com.github.novotnyr.aedile.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBasedConfigurationNameResolver implements ConfigurationNameResolver {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigurationNameResolver delegateConfigurationNameResolver = new DefaultConfigurationNameResolver();

    private final Map<String, String> configurationNameMapping = new LinkedHashMap<>();

    public MapBasedConfigurationNameResolver() {
        // empty constructor
    }

    public MapBasedConfigurationNameResolver(Map<String, String> configurationNameMapping) {
        this.configurationNameMapping.putAll(configurationNameMapping);
    }

    @Override
    public String getConfigurationName(File configurationFile) {
        String configurationName = delegateConfigurationNameResolver.getConfigurationName(configurationFile);
        String resolvedConfigurationName = configurationNameMapping.getOrDefault(configurationName, configurationName);
        logger.debug("Resolved configuration name '{}' to '{}'", configurationName, resolvedConfigurationName);
        return resolvedConfigurationName;
    }
}
