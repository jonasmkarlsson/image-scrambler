package org.jonasmkarlsson.imagescrambler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.jonasmkarlsson.imagescrambler.Constants;
import org.jonasmkarlsson.imagescrambler.Launcher;
import org.junit.Test;

public class LauncherTest extends AbstractCliTest {

    private final String IMAGE_FILENAME = "SL271541";
    private final String IMAGE_EXT = ".jpg";
    private final String IMAGE_PATH = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "images" + File.separator + IMAGE_FILENAME + IMAGE_EXT;

    @Test
    public void testRunWithParseException() {
        String[] args = { "-parse" };
        new Launcher().run(args);
        assertEquals("Encountered exception while parsing using GnuParser: Unrecognized option: -parse" + separator, outContent.toString());
    }

    @Test
    public void testRunWithImageFileDoesNotExists() {
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], "THIS_IMAGE_DOES_NOT_EXISTS.JPG",
                "-" + Constants.OPTIONS_GREY[0]};
        // @formatter:on
        new Launcher().run(args);
        assertEquals("Encountered IO exception while trying to read file: Can't read input file!" + separator, outContent.toString());
    }

    @Test
    public void testRunWithShortVersionArgument() {
        String[] args = { "-v" };
        new Launcher().run(args);
        assertEquals(new Launcher().getApplication() + separator, outContent.toString());
    }

    @Test
    public void testRunWithLongVersionArgument() {
        String[] args = { "-version" };
        new Launcher().run(args);
        assertEquals(new Launcher().getApplication() + separator, outContent.toString());
    }

    @Test
    public void testPuzzleWithDefaultColumnsAndRows() {
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH,
                "-" + Constants.OPTIONS_PUZZLE[0]};
        // @formatter:on
        new Launcher().run(args);
        assertTrue(fileExists(IMAGE_FILENAME + "-" + Constants.OPTIONS_PUZZLE[1] + IMAGE_EXT));
    }

    @Test
    public void testPuzzleWithColumnsAndRowsValues() {
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH,
                "-" + Constants.OPTIONS_PUZZLE[0], "10,7"};
        // @formatter:on
        new Launcher().run(args);
        assertTrue(fileExists(IMAGE_FILENAME + "-" + Constants.OPTIONS_PUZZLE[1] + IMAGE_EXT));
    }

    @Test
    public void testFlipVertically() {
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH,
                "-" + Constants.OPTIONS_FLIP_VERTICALLY[0]};
        // @formatter:on
        new Launcher().run(args);
        assertTrue(fileExists(IMAGE_FILENAME + "-" + Constants.OPTIONS_FLIP_VERTICALLY[1] + IMAGE_EXT));
    }

    @Test
    public void testFlipHorizontelly() {
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH,
                "-" + Constants.OPTIONS_FLIP_HORIZONTALLY[0]};
        // @formatter:on
        new Launcher().run(args);
        assertTrue(fileExists(IMAGE_FILENAME + "-" + Constants.OPTIONS_FLIP_HORIZONTALLY[1] + IMAGE_EXT));
    }

    @Test
    public void testGray() {
        // @formatter:off∫
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH,
                "-" + Constants.OPTIONS_GREY[0]};
        // @formatter:on
        new Launcher().run(args);
        assertTrue(fileExists(IMAGE_FILENAME + "-" + Constants.OPTIONS_GREY[1] + IMAGE_EXT));
    }

    /**
     * Check if the specified file path exists.
     * 
     * @param filePath the file path.
     * @return true if file exists and are not a directory, either wise false.
     */
    private boolean fileExists(final String filePath) {
        File f = new File(filePath);
        return (f.exists() && !f.isDirectory());
    }

}
