package org.jonasmkarlsson.imagescrambler;

/**
 * Main class, aka. Command Line Interface (CLI) for the Image Scrambler application. The only purpose of this class are to start the method
 * {@link org.jonasmkarlsson.imagescrambler.Launcher#run(String[])} with the given command line arguments. In short, this class are only used when using the CLI
 * from a operative system prompt/terminal.
 * 
 * @author Jonas M Karlsson
 */
public class Main {

    /**
     * Default constructor.
     */
    @SuppressWarnings("all")
    public Main() {
        super();
    }

    /**
     * Main executable method.
     * 
     * @param commandLineArguments the command line arguments.
     */
    public static void main(final String[] commandLineArguments) {
        new Launcher().run(commandLineArguments);
    }

}
