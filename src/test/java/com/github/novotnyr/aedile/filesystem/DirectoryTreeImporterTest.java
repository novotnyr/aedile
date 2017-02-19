package com.github.novotnyr.aedile.filesystem;

import com.ecwid.consul.v1.ConsulClient;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DirectoryTreeImporterTest {
    private ConsulConfigurationRepository repository;

    private ConsulClient consulClient;

    private ConsulConfiguration configuration;

    private DirectoryTreeImporter importer;

    @Before
    public void setUp() throws Exception {
        configuration = new ConsulConfiguration();

        consulClient = mock(ConsulClient.class);

        repository = new ConsulConfigurationRepository(configuration) {
            @Override
            protected ConsulClient getConsulClient() {
                return consulClient;
            }
        };

        importer = new DirectoryTreeImporter(repository);
    }

    @Test
    public void testImport() throws Exception {
        importer.run(new File("src/test/resources/datacenters/dc2"));

        verify(consulClient, times(1)).setKVValue(eq("config/prod/application/name"), eq("mars"), any(), any(), any());
        verify(consulClient, times(1)).setKVValue(eq("config/prod/impl/version"), eq("1"), any(), any(), any());


    }
}