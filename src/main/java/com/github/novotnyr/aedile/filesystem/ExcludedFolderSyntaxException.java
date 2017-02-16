package com.github.novotnyr.aedile.filesystem;

/**
 * Indicates a wrong syntax of excluded folder specification.
 */
public class ExcludedFolderSyntaxException extends RuntimeException {

    public ExcludedFolderSyntaxException() {
        super();
    }

    public ExcludedFolderSyntaxException(String msg) {
        super(msg);
    }

    public ExcludedFolderSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcludedFolderSyntaxException(Throwable cause) {
        super(cause);
    }
}