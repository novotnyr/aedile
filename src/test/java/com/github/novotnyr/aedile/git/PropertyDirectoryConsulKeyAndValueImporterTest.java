package com.github.novotnyr.aedile.git;

import com.ecwid.consul.v1.ConsulClient;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PropertyDirectoryConsulKeyAndValueImporterTest {
    private ConsulConfigurationRepository repository;

    private ConsulClient consulClient;

    private PropertyDirectoryConsulKeyAndValueImporter importer;

    private File implConfigFolder;

    private File apiConfigFolder;

    private File envConfigPath;

    private File apiConfig;

    private File implConfig;

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

        importer = new PropertyDirectoryConsulKeyAndValueImporter(repository);

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
        implConfig = new File(implConfigFolder, "application.properties");

        Files.write(apiConfig.toPath(), Arrays.asList("version=1.0", "api=true"));
        Files.write(implConfig.toPath(), Arrays.asList("version=1.0", "impl=true"));
    }

    @Test
    public void testRun() throws Exception {
        importer.run(implConfigFolder);

        verify(consulClient, times(1)).setKVValue(eq("config/application/version"), eq("1.0"), any(), any(), any());
        verify(consulClient, times(1)).setKVValue(eq("config/application/impl"), eq("true"), any(), any(), any());
    }


}