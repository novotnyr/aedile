package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfiguration;
import org.junit.Test;

import java.io.File;

public class DatacenterFilesystemImporterTest {
    @Test
    public void testImport() throws Exception {
        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter() {
            @Override
            protected ConsulConfiguration getConsulConfiguration() {
                return new ConsulConfiguration();
            }
        };

        importer.importDatacenters(new File("src/test/resources/datacenters"));
    }

    @Test
    public void testImportWithIgnores() throws Exception {
        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter() {
            @Override
            protected ConsulConfiguration getConsulConfiguration() {
                return new ConsulConfiguration();
            }

            @Override
            protected String getExcludedFoldersSpecificationString() {
                return "dc1#config/unittest2/application:dc1#config/unittest/application";
            }
        };
        importer.configureExcludedFolders();


        importer.importDatacenters(new File("src/test/resources/datacenters"));
    }

    @Test
    public void testImportWithIgnoresPattern() throws Exception {
        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter() {
            @Override
            protected ConsulConfiguration getConsulConfiguration() {
                return new ConsulConfiguration();
            }

            @Override
            protected String getExcludedFoldersSpecificationString() {
                return "dc1#config/**/*";
            }
        };
        importer.configureExcludedFolders();


        importer.importDatacenters(new File("src/test/resources/datacenters"));
    }
}