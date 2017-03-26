package org.jonasmkarlsson.imagescrambler.test;

import static org.junit.Assert.assertEquals;

import org.jonasmkarlsson.imagescrambler.Launcher;
import org.jonasmkarlsson.imagescrambler.Main;
import org.junit.Test;

public class MainTest extends AbstractCliTest {

    @Test
    public void testRunWithNullAsArgument() {
        new Main();
        Main.main(null);
        assertEquals(gnu, outContent.toString());
    }

    @Test
    public void testRunWithEmptyArguments() {
        String[] args = { "" };
        new Launcher().run(args);
        assertEquals(gnu, outContent.toString());
    }

}
