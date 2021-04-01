package com.github.novotnyr.aedile.cmdline;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.MapOptionHandler;

import java.util.Map;

public class FilesystemImportCommand {
    @Argument(metaVar = "directory")
    private String directory;

    @Option(name = "--no-recurse")
    private boolean recursiveImportDisabled;

    @Option(name = "--remap-config-name", handler = MapOptionHandler.class)
    private Map<String, String> configurationNameMapping;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isRecursiveImportDisabled() {
        return this.recursiveImportDisabled;
    }

    public void setRecursiveImportDisabled(boolean recursiveImportDisabled) {
        this.recursiveImportDisabled = recursiveImportDisabled;
    }

    public Map<String, String> getConfigurationNameMapping() {
        return configurationNameMapping;
    }

    public void setConfigurationNameMapping(Map<String, String> configurationNameMapping) {
        this.configurationNameMapping = configurationNameMapping;
    }
}