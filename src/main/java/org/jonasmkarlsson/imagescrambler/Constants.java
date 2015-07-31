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
    public static final String DEFAULT_PUZZLE_DELIMITER = ",";

    // Options section...
    public static final String[] OPTIONS_FLIP_VERTICALLY = { "fv", "flipv" };
    public static final String[] OPTIONS_FLIP_HORIZONTALLY = { "fh", "fliph" };
    public static final String[] OPTIONS_GREY = { "g", "gray" };
    public static final String[] OPTIONS_IMAGE = { "i", "image" };
    public static final String[] OPTIONS_PUZZLE = { "pz", "puzzle" };
    public static final String[] OPTIONS_VERSION = { "v", "version" };

    // Help section...
    public static final String HELP_FLIP_VERTICALLY = "Flipes image vertically (upside down).";
    public static final String HELP_FLIP_HORIZONTALLY = "Flipes image horizontally (left right).";
    public static final String HELP_GRAY = "Make image grey-isch (black & white).";
    public static final String HELP_IMAGE = "Image to scramble.";
    public static final String HELP_PUZZLE = "Puzzles image.";
    public static final String HELP_VERSION = "Display version information.";

    private Constants() {
        super();
    }

}
