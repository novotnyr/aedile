package com.github.novotnyr.aedile.filesystem;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DatacenterFilesystemImporterTest {
    private ConsulConfigurationRepository repository;

    private ConsulClient consulClient;

    private ConsulConfiguration configuration;

    private DatacenterFilesystemImporter importer;

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

        importer = new DatacenterFilesystemImporter() {
            @Override
            protected ConsulConfiguration getConsulConfiguration() {
                return configuration;
            }

            @Override
            protected ConsulConfigurationRepository getConsulConfigurationRepository(ConsulConfiguration consulConfiguration) {
                return repository;
            }
        };
    }

    @Test
    public void testImport() throws Exception {
        importer.importDatacenters(new File("src/test/resources/datacenters"));

        verify(consulClient, times(1)).setKVValue(eq("config/unittest/application/code/javac"), eq("8"), any(), any(), argThat(this::isDataCenter1));
        verify(consulClient, times(1)).setKVValue(eq("config/unittest/application/demo/version"), eq("1"), any(), any(), argThat(this::isDataCenter1));
        verify(consulClient, times(1)).setKVValue(eq("config/unittest2/application/demo2/version"), eq("2"), any(), any(), argThat(this::isDataCenter1));

        verify(consulClient, times(1)).setKVValue(eq("config/prod/application/name"), eq("mars"), any(), any(), argThat(this::isDataCenter2));
        verify(consulClient, times(1)).setKVValue(eq("config/prod/impl/version"), eq("1"), any(), any(), argThat(this::isDataCenter2));
    }

    @Test
    public void testImportWithIgnores() throws Exception {
        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter() {
            @Override
            protected ConsulConfiguration getConsulConfiguration() {
                return configuration;
            }

            @Override
            protected ConsulConfigurationRepository getConsulConfigurationRepository(ConsulConfiguration consulConfiguration) {
                return repository;
            }

            @Override
            protected String getExcludedFoldersSpecificationString() {
                return "dc1#config/unittest2/application:dc1#config/unittest/application";
            }
        };
        importer.configureExcludedFolders();


        importer.importDatacenters(new File("src/test/resources/datacenters"));

        verify(consulClient, times(1)).setKVValue(eq("config/prod/application/name"), eq("mars"), any(), any(), argThat(this::isDataCenter2));
        verify(consulClient, times(1)).setKVValue(eq("config/prod/impl/version"), eq("1"), any(), any(), argThat(this::isDataCenter2));

    }

    @Test
    public void testImportWithIgnoresPattern() throws Exception {
        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter() {
            @Override
            protected ConsulConfiguration getConsulConfiguration() {
                return configuration;
            }

            @Override
            protected ConsulConfigurationRepository getConsulConfigurationRepository(ConsulConfiguration consulConfiguration) {
                return repository;
            }

            @Override
            protected String getExcludedFoldersSpecificationString() {
                return "dc1#config/**/*";
            }
        };
        importer.configureExcludedFolders();


        importer.importDatacenters(new File("src/test/resources/datacenters"));

        verify(consulClient, times(1)).setKVValue(eq("config/prod/application/name"), eq("mars"), any(), any(), argThat(this::isDataCenter2));
        verify(consulClient, times(1)).setKVValue(eq("config/prod/impl/version"), eq("1"), any(), any(), argThat(this::isDataCenter2));

    }

    private boolean isDataCenter1(QueryParams queryParams) {
        return queryParams.getDatacenter().equals("dc1");
    }

    private boolean isDataCenter2(QueryParams queryParams) {
        return queryParams.getDatacenter().equals("dc2");
    }
}