package com.github.novotnyr.aedile;

public class ImporterConfigurationException extends RuntimeException {

    public ImporterConfigurationException() {
        super();
    }

    public ImporterConfigurationException(String msg) {
        super(msg);
    }

    public ImporterConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImporterConfigurationException(Throwable cause) {
        super(cause);
    }
}