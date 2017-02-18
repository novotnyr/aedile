package com.github.novotnyr.aedile.util;

import java.io.File;
import java.io.IOException;

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
}
