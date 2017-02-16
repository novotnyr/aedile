package com.github.novotnyr.aedile.filesystem;

/**
 * Represents a folder excluded from import
 */
public class ExcludedFolder {
    private String datacenter;

    private String folder;

    public ExcludedFolder(String datacenter, String folder) {
        this.datacenter = datacenter;
        this.folder = folder;
    }

    /**
     * Parses the excluded definition from a string. Example:
     * <pre>
     *     datacenter#folder
     * </pre>
     *
     *
     * @param definition a hash-separated pair of datacenter name and the excluded folder name
     * @return parsed {@link ExcludedFolder} specification
     * @throws ExcludedFolderSyntaxException when the syntax of the definition is wrong.
     */
    public static ExcludedFolder parse(String definition) throws ExcludedFolderSyntaxException {
        String[] components = definition.split("#");
        if (components.length != 2) {
            throw new ExcludedFolderSyntaxException("Illegal syntax for definition [" + definition + "]");
        }
        return new ExcludedFolder(components[0], components[1]);
    }

    /**
     * Get the datacenter of the excluded folder
     * @return the datacenter.
     */
    public String getDatacenter() {
        return datacenter;
    }

    /**
     * Get the folder name of the excluded folder.
     * @return the folder name
     */
    public String getFolder() {
        return folder;
    }
}