package com.github.novotnyr.aedile.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileUtils {
    public static void mkdirs(String path) throws IOException {
        File pathFile = new File(path);
        if(pathFile.exists()) {
            return;
        }

        boolean wasCreated = pathFile.mkdirs();
        if(!wasCreated) {
            throw new IOException("Cannot create directories in path " + pathFile);
        }
    }

    /**
     * Indicates whether the path corresponds to a regular file
     * @param path filesystem path
     * @return true if this is a regular file path
     * @see File#isFile()
     */
    public static boolean isFile(Path path) {
        return path.toFile().isFile();
    }

    /**
     * Removes common path prefix that is shared between parent file and child file.
     * <p>
     *     If the <i>parent</i> is <code>/var/log/data</code> and child is
     *     <code>/var/log/data/config/java</code>, then the removed prefix
     *     returned from method is <code>config/java</code>
     * </p>
     * <p>
     *     Note that the first character of the remaining string is removed as well.
     *     See the example, where the leading slash is removed.
     * </p>
     * @param parent parent file that is prefix of the child
     * @param child child file that shares a common prefix with parent
     * @return remains of path without prefix
     */
    public static String removeCommonPathPrefix(File parent, File child) {
        StringBuilder parentFullName = new StringBuilder(parent.toString());
        StringBuilder childFullName = new StringBuilder(child.toString());

        StringBuilder suffix = childFullName.delete(0, parentFullName.length() + 1);
        return suffix.toString();
    }
}
