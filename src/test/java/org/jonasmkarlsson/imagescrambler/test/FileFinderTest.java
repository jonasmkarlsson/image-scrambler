package org.jonasmkarlsson.imagescrambler.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jonasmkarlsson.imagescrambler.FileFinder;
import org.junit.Test;

public class FileFinderTest {

    Path startingPath = Paths.get("src/test/resources");
    
    @Test
    public void testFindJpgFiles() throws IOException {
        FileFinder fileFinder = new FileFinder("*.jpg", true);
        Files.walkFileTree(startingPath, fileFinder);
        assertEquals(2,fileFinder.getMatchedFiles().size());
    }

    @Test
    public void testFindFile() throws IOException {
        FileFinder fileFinder = new FileFinder("SL271541.jpg", true);
        Files.walkFileTree(startingPath, fileFinder);
        assertEquals(1,fileFinder.getMatchedFiles().size());
    }

}
