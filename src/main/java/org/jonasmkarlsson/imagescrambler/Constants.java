package org.jonasmkarlsson.imagescrambler;

/**
 * Contains global constants used within the image-scrambler application.
 */
public class Constants {

    // Properties settings...
    public static final String PROPERTY_APPLICATION = "application";
    public static final String PROPERTY_COMMAND = "command";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VERSION = "version";
    public static final String PROPERTY_FILE = "imagescrambler.properties";

    // Default settings...
    public static final int DEFAULT_PUZZLE_COLUMNS_ROWS = 5;
    // public static final String DEFAULT_PUZZLE_DELIMITER = ",";
    public static final String DEFAULT_DIRECTORY = ".";

    // Options section for...
    public static final String[] OPTIONS_DIRECTORY = { "d", "directory" };
    public static final String[] OPTIONS_FLIP_VERTICALLY = { "fv", "flipv" };
    public static final String[] OPTIONS_FLIP_HORIZONTALLY = { "fh", "fliph" };
    public static final String[] OPTIONS_GREY = { "g", "gray" };
    public static final String[] OPTIONS_OUTPUT_DIRECTORY = { "o", "output" };
    public static final String[] OPTIONS_RECURSIVE = { "r", "recursive" };
    public static final String[] OPTIONS_PATTERN = { "p", "pattern" };
    public static final String[] OPTIONS_PUZZLE = { "pz", "puzzle" };
    public static final String[] OPTIONS_PUZZLE_COLUMNS = { "c", "columns" };
    public static final String[] OPTIONS_PUZZLE_ROWS = { "r", "rows" };
    public static final String[] OPTIONS_PRUNE = { "pr", "prune" };
    public static final String[] OPTIONS_VERSION = { "v", "version" };

    // Help section...
    public static final String HELP_DIRECTORY = "Directory to search for pattern.";
    public static final String HELP_FLIP_VERTICALLY = "Flipes image vertically (upside down).";
    public static final String HELP_FLIP_HORIZONTALLY = "Flipes image horizontally (left right).";
    public static final String HELP_GRAY = "Make image grey-isch (black & white).";
    public static final String HELP_OUTPUT_DIRECTORY = "Output directory where to save scrambled images.";
    public static final String HELP_RECURSIVE = "Look in subfolders";
    public static final String HELP_PATTERN = "Pattern to scramble.";
    public static final String HELP_PUZZLE = "Puzzles image(s).";
    public static final String HELP_PUZZLE_COLUMNS = "Number of columns, only usable with puzzle option.";
    public static final String HELP_PUZZLE_ROWS = "Number of rows, only usable with puzzle option.";
    public static final String HELP_PRUNE = "Following symlinks.";
    public static final String HELP_VERSION = "Display version information.";

    // Default constructor
    private Constants() {
        super();
    }

}
