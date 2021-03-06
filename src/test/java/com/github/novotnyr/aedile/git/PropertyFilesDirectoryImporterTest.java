package com.github.novotnyr.aedile.git;

import com.ecwid.consul.v1.ConsulClient;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.filesystem.PropertyFilesDirectoryImporter;
import com.github.novotnyr.aedile.resolver.MapBasedConfigurationNameResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PropertyFilesDirectoryImporterTest {
    private ConsulConfigurationRepository repository;

    private ConsulClient consulClient;

    private PropertyFilesDirectoryImporter importer;

    private File implConfigFolder;

    private File apiConfigFolder;

    private File envConfigPath;

    private File apiConfig;

    private File implConfig;

    private File localProfileApiConfig;

    @Before
    public void setUp() throws Exception {
        ConsulConfiguration configuration = new ConsulConfiguration();

        consulClient = mock(ConsulClient.class);

        repository = new ConsulConfigurationRepository(configuration) {
            @Override
            protected ConsulClient getConsulClient() {
                return consulClient;
            }
        };

        importer = new PropertyFilesDirectoryImporter(repository);

        setupFiles();
    }

    private void setupFiles() throws IOException {
        Path configParentPath = Files.createTempDirectory("unit-test");

        envConfigPath = new File(configParentPath.toFile(), "env");

        apiConfigFolder = new File(envConfigPath, "api");
        Assert.assertTrue(apiConfigFolder.mkdirs());

        implConfigFolder = new File(envConfigPath, "impl");
        Assert.assertTrue(implConfigFolder.mkdirs());

        apiConfig = new File(apiConfigFolder, "application.properties");
        localProfileApiConfig = new File(apiConfigFolder, "application-local.properties");

        implConfig = new File(implConfigFolder, "application.properties");

        Files.write(apiConfig.toPath(), Arrays.asList("version=1.0", "api=true"));
        Files.write(localProfileApiConfig.toPath(), Arrays.asList("version=1.0", "api=true", "profile=local"));
        Files.write(implConfig.toPath(), Arrays.asList("version=1.0", "impl=true"));
    }

    @Test
    public void testRun() throws Exception {
        importer.run(implConfigFolder);

        verify(consulClient, times(1)).setKVValue(eq("config/application/version"), eq("1.0"), any(), any(), any());
        verify(consulClient, times(1)).setKVValue(eq("config/application/impl"), eq("true"), any(), any(), any());
    }

    @Test
    public void testRunWithAliasing() throws Exception {
        MapBasedConfigurationNameResolver resolver = new MapBasedConfigurationNameResolver(Collections.singletonMap("application-local", "application,local"));
        importer = new PropertyFilesDirectoryImporter(repository, resolver);
        importer.run(apiConfigFolder);

        verify(consulClient, times(1)).setKVValue(eq("config/application,local/profile"), eq("local"), any(), any(), any());
    }

}