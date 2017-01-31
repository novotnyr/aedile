package com.github.novotnyr.aedile.git.command;

import com.github.novotnyr.aedile.ImporterConfigurationException;
import com.github.novotnyr.aedile.git.ConfigurationImportException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GitClone {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    private File clonedRepositoryDirectory;

    private String remoteUrl;

    private boolean isCleaningBeforeClone = false;

    public void run() {
        try {
            validateConfiguration();

            File localPath = clonedRepositoryDirectory;
            if(isCleaningBeforeClone) {
                localPath.delete();
            }

            // then clone
            logger.info("Cloning from " + remoteUrl + " to " + localPath);
            try (Git result = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(localPath)
                    .call()) {
                // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                logger.info("Having repository: " + result.getRepository().getDirectory());
            }
        } catch (GitAPIException e) {
            throw new ConfigurationImportException("Unable to clone Git repo folder", e);
        }
    }

    private void validateConfiguration() {
        if(clonedRepositoryDirectory == null) {
            throw new ImporterConfigurationException("Cloned repository directory must be set");
        }
    }

    public void setClonedRepositoryDirectory(File clonedRepositoryDirectory) {
        this.clonedRepositoryDirectory = clonedRepositoryDirectory;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public void setCleaningBeforeClone(boolean cleaningBeforeClone) {
        isCleaningBeforeClone = cleaningBeforeClone;
    }
}
