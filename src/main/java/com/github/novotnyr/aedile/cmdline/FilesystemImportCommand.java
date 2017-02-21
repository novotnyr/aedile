package com.github.novotnyr.aedile.cmdline;

import org.kohsuke.args4j.Argument;

public class FilesystemImportCommand {
    @Argument(metaVar = "directory")
    private String directory;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}