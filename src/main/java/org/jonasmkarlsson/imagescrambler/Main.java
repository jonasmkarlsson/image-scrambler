package org.jonasmkarlsson.imagescrambler;

/**
 * Command Line Interface (CLI) for Generate Data project.
 * 
 * @author Jonas M Karlsson
 */
public class Main {

    @SuppressWarnings("all")
    public Main(){
        
    }
    
    /**
     * Main executable method .
     * 
     * @param commandLineArguments the command line arguments.
     */
    public static void main(final String[] commandLineArguments) {
        new Launcher().run(commandLineArguments);
    }

}
