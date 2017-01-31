package com.github.novotnyr.aedile.git.command;

import com.github.novotnyr.aedile.git.ConfigurationImportException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GitPull {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    private File repositoryFile;

    public void run() {
        try {
            Git
                .open(repositoryFile)
                .pull()
                .call();
        } catch (GitAPIException e) {
            throw new ConfigurationImportException("Unable to clone Git repo folder", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRepositoryFile(File repositoryFile) {
        this.repositoryFile = repositoryFile;
    }
}
