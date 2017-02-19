package com.github.novotnyr.aedile.cmdline;

import org.kohsuke.args4j.Option;

public class FilesystemImportDatacenterCommand {
    @Option(name = "directory", required = true)
    private String directory;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
