package com.github.novotnyr.aedile.cmdline;

import org.kohsuke.args4j.Argument;

import java.io.File;

public class GitImportConfiguration {
    @Argument(required = true, index = 0, usage = "JSON config file", metaVar = "JSON configuration file")
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
