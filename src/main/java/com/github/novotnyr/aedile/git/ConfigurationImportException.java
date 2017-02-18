package com.github.novotnyr.aedile.git;

/**
 * Indicate an error in the importing of external configuration.
 */
public class ConfigurationImportException extends RuntimeException {

    public ConfigurationImportException() {
        super();
    }

    public ConfigurationImportException(String msg) {
        super(msg);
    }

    public ConfigurationImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationImportException(Throwable cause) {
        super(cause);
    }
}