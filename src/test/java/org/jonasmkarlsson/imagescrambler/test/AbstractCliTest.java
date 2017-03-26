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
    public final String gnu = "usage: java -jar image-scrambler.jar [-c <arg>] [-d <arg>] [-fh] [-fv] [-g] [-o <arg>] [-p <arg>] [-pr] [-pz <arg>] [-r" + separator
                + "       <arg>] [-v]" + separator
                + "Help GNU" + separator
                + "     -c,--columns <arg>     " + Constants.HELP_PUZZLE_COLUMNS + separator
                + "     -d,--directory <arg>   " + Constants.HELP_DIRECTORY + separator
                + "     -fh,--fliph            " + Constants.HELP_FLIP_HORIZONTALLY + separator
                + "     -fv,--flipv            " + Constants.HELP_FLIP_VERTICALLY + separator
                + "     -g,--gray              " + Constants.HELP_GRAY + separator
                + "     -o,--output <arg>      " + Constants.HELP_OUTPUT_DIRECTORY + separator
                + "     -p,--pattern <arg>     " + Constants.HELP_PATTERN + separator
                + "     -pr,--prune            " + Constants.HELP_PRUNE + separator
                + "     -pz,--puzzle <arg>     " + Constants.HELP_PUZZLE + separator
                + "     -r,--rows <arg>        " + Constants.HELP_PUZZLE_ROWS + separator
                + "     -v,--version           " + Constants.HELP_VERSION + separator
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
