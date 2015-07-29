package org.jonasmkarlsson.imagescrambler.test;

import java.io.File;

import org.jonasmkarlsson.imagescrambler.Constants;
import org.jonasmkarlsson.imagescrambler.Launcher;
import org.junit.Test;

public class LauncherTest {

    private final String IMAGE_PATH = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "images" + File.separator;

    @Test
    public void testPuzzle() {
        // puzzle: image-scrambler -p *.jpg -d src/test/resources/images -o ./target/test/launcher/puzzle -puzzle 15 15
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH + "SL271541.jpg",
                "-" + Constants.OPTIONS_PUZZLE[0], "5,15"};
        // @formatter:on
        new Launcher().run(args);
    }

    @Test
    public void testFlip() {
        // flip: image-scrambler -p *.jpg -d src/test/resources/images -o ./target/test/launcher/flip -flip
        // @formatter:off
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH + "SL271541.jpg",
                "-" + Constants.OPTIONS_FLIP_VERTICALLY[0]};
        // @formatter:on
        new Launcher().run(args);
    }

    @Test
    public void testGray() {
        // gray: image-scrambler -p *.jpg -d src/test/resources/images -o ./target/test/launcher/gray -gray
        // @formatter:off∫
        String[] args = { 
                "-" + Constants.OPTIONS_IMAGE[0], IMAGE_PATH + "SL271541.jpg",
                "-" + Constants.OPTIONS_GREY[0]};
        // @formatter:on
        new Launcher().run(args);
    }

}
