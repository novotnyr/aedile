package com.github.novotnyr.aedile.cmdline;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class FilesystemImportCommand {
    @Argument(metaVar = "directory")
    private String directory;

    @Option(name = "--no-recurse")
    private boolean recursiveImportDisabled;

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
}