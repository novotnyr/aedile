package com.github.novotnyr.aedile.resolver;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapBasedConfigurationNameResolverTest {
    private ConfigurationNameResolver configurationNameResolver;

    @Before
    public void setUp() throws Exception {
        Map<String, String> configurationNameMapping = new LinkedHashMap<>();
        configurationNameMapping.put("application-local", "application,local");

        configurationNameResolver = new MapBasedConfigurationNameResolver(configurationNameMapping);
    }

    @Test
    public void testResolver() {
        String applicationConfiguration = configurationNameResolver.getConfigurationName(new File("/tmp/application.properties"));
        assertEquals("application", applicationConfiguration);

        String applicationLocalConfiguration = configurationNameResolver.getConfigurationName(new File("/tmp/application-local.properties"));
        assertEquals("application,local", applicationLocalConfiguration);
    }
}