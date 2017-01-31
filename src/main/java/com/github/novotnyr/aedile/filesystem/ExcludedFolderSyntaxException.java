package com.github.novotnyr.aedile.filesystem;

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