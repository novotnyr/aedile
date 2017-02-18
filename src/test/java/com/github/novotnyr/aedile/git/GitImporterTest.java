package com.github.novotnyr.aedile.git;

import com.github.novotnyr.aedile.util.FileUtils;
import org.junit.Test;

import java.io.File;

public class GitImporterTest {
    @Test
    public void test() throws Exception {
        FileUtils.mkdirs("./target/gitrepo");

        GitImporter gitImporter = new GitImporter();
        gitImporter.run(new File("src/test/resources/config.json"));
    }
}