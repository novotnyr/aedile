package com.github.novotnyr.aedile.filesystem;

public class ExcludedFolder {
    private String datacenter;

    private String folder;

    public ExcludedFolder(String datacenter, String folder) {
        this.datacenter = datacenter;
        this.folder = folder;
    }

    public static ExcludedFolder parse(String definition) throws ExcludedFolderSyntaxException {
        String[] components = definition.split("#");
        if (components.length != 2) {
            throw new ExcludedFolderSyntaxException("Illegal syntax for definition [" + definition + "]");
        }
        return new ExcludedFolder(components[0], components[1]);
    }

    public String getDatacenter() {
        return datacenter;
    }

    public String getFolder() {
        return folder;
    }
}