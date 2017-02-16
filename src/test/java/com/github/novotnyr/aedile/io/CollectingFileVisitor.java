package com.github.novotnyr.aedile.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class CollectingFileVisitor extends SimpleFileVisitor<Path> {
    private List<File> files = new LinkedList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(Files.isRegularFile(file)) {
            files.add(file.toFile());
        }

        return super.visitFile(file, attrs);
    }

    public List<File> getFiles() {
        return files;
    }
}
