package org.jonasmkarlsson.imagescrambler.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jonasmkarlsson.imagescrambler.Constants;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractCliTest {

    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    protected final String separator = System.getProperty("line.separator");

    // @formatter:off
    public final String gnu = "usage: java -jar image-scrambler.jar [-fh] [-fv] [-g] [-i <arg>] [-pz <arg>] [-v]" + separator 
                + "Help GNU" + separator
                + "     -fh,--fliph          " + Constants.HELP_FLIP_HORIZONTALLY + separator
                + "     -fv,--flipv          " + Constants.HELP_FLIP_VERTICALLY + separator
                + "     -g,--gray            " + Constants.HELP_GRAY + separator
                + "     -i,--image <arg>     " + Constants.HELP_IMAGE + separator
                + "     -pz,--puzzle <arg>   " + Constants.HELP_PUZZLE + separator
                + "     -v,--version         " + Constants.HELP_VERSION + separator
                + "End of GNU Help" + separator;
    // @formatter:on

    /**
     * Don't write anything to System.out when doing tests against outContent, because outContent will catch every System.out print command.
     */
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() {
        System.setOut(null);
        System.setErr(null);
    }

}
