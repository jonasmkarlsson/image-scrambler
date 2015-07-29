package org.jonasmkarlsson.imagescrambler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class Launcher {
    private static final Logger LOGGER = Logger.getLogger(Launcher.class);

    private final Properties properties;
    private final String application;
    private final String command;

    private Random rand = new Random();

    public Launcher() {
        super();
        properties = readProperties();
        application = properties.getProperty(Constants.PROPERTY_APPLICATION);
        command = properties.getProperty(Constants.PROPERTY_COMMAND);
    }

    /**
     * Read properties file
     * 
     * @return
     */
    @SuppressWarnings("all")
    private Properties readProperties() {
        Properties prop = new Properties();
        InputStream stream = this.getClass().getResourceAsStream(Constants.PROPERTY_FILE);
        try {
            prop.load(stream);
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Encountered exception while reading property file '" + Constants.PROPERTY_FILE + "':", e);
            }
            LOGGER.info("Encountered exception while reading property file '" + Constants.PROPERTY_FILE + "'. See log file for more information.");
        }
        return prop;
    }

    /**
     * Parse the command line arguments
     * 
     * @param commandLineArguments the arguments to CLI
     */
    @SuppressWarnings("all")
    public void run(String[] commandLineArguments) {
        final Options gnuOptions = constructGnuOptions();
        final CommandLineParser cmdLineGnuParser = new GnuParser();
        try {
            CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);
            useGnuParser(commandLine);
        } catch (ParseException parseException) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Encountered exception while parsing using GnuParser:", parseException);
            }
            System.out.println("Encountered exception while parsing using GnuParser: " + parseException.getMessage());
        } catch (IOException ioException) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Encountered IO exception while trying to read file: ", ioException);
            }
            System.out.println("Encountered IO exception while trying to read file: " + ioException.getMessage());
        }
    }

    /**
     * Parse the command line and execute the action.
     * 
     * @param commandLine
     * @throws IOException
     */
    private void useGnuParser(final CommandLine commandLine) throws IOException {
        // Find each parameter...
        boolean image = commandLine.hasOption(Constants.OPTIONS_IMAGE[0]) || commandLine.hasOption(Constants.OPTIONS_IMAGE[1]);
        boolean version = commandLine.hasOption(Constants.OPTIONS_VERSION[0]) || commandLine.hasOption(Constants.OPTIONS_VERSION[1]);

        if (version || !image) {
            printHelp(constructGnuOptions(), 120, "Help GNU", "End of GNU Help", 5, 3, true);
        } else {
            boolean flipV = commandLine.hasOption(Constants.OPTIONS_FLIP_VERTICALLY[0]) || commandLine.hasOption(Constants.OPTIONS_FLIP_VERTICALLY[1]);
            boolean flipH = commandLine.hasOption(Constants.OPTIONS_FLIP_HORIZONTALLY[0]) || commandLine.hasOption(Constants.OPTIONS_FLIP_HORIZONTALLY[1]);
            boolean grey = commandLine.hasOption(Constants.OPTIONS_GREY[0]) || commandLine.hasOption(Constants.OPTIONS_GREY[1]);

            boolean puzzle = commandLine.hasOption(Constants.OPTIONS_PUZZLE[0]) || commandLine.hasOption(Constants.OPTIONS_PUZZLE[1]);
            int puzzleRows = Constants.DEFAULT_PUZZLE_COLUMNS_ROWS;
            int puzzleCols = Constants.DEFAULT_PUZZLE_COLUMNS_ROWS;

            // Fetch image to scramble...
            Path path = FileSystems.getDefault().getPath(checkCommandLineForOption(Constants.OPTIONS_IMAGE, commandLine));

            if (puzzle) {
                String[] puzzleOptionValues = checkCommandLineForOptions(Constants.OPTIONS_PUZZLE, commandLine, Constants.DEFAULT_PUZZLE_DELIMITER);
                if (puzzleOptionValues.length > 0) {
                    puzzleCols = Integer.valueOf(puzzleOptionValues[0]);
                }

                if (puzzleOptionValues.length > 1) {
                    puzzleRows = Integer.valueOf(puzzleOptionValues[1]);
                }
            }

            scramble(path, flipV, flipH, grey, puzzle, puzzleCols, puzzleRows);
        }
    }

    private void scramble(final Path path, final boolean flipV, final boolean flipH, final boolean grey, final boolean puzzle, final int puzzleCols, final int puzzleRows) {
        try {
            LOGGER.info("Attempting to scrambling path '" + path.toAbsolutePath() + "'");
            BufferedImage image = ImageIO.read(path.toFile());
            String newFileName = getFileName(path);

            if (flipV) {
                image = this.flipVertically(image);
                newFileName = newFileName + "-" + Constants.OPTIONS_FLIP_VERTICALLY[1];
            }

            if (flipH) {
                image = this.flipHorizontally(image);
                newFileName = newFileName + "-" + Constants.OPTIONS_FLIP_HORIZONTALLY[1];
            }

            if (grey) {
                image = gray(image);
                newFileName = newFileName + "-" + Constants.OPTIONS_GREY[1];
            }

            if (puzzle) {
                image = puzzle(image, puzzleCols, puzzleRows);
                newFileName = newFileName + "-" + Constants.OPTIONS_PUZZLE[1];
            }

            String ext = this.getExtension(path);
            newFileName = newFileName + "." + ext;
            LOGGER.info("Saving scrambled file as '" + newFileName + "'");
            ImageIO.write(image, ext, new File(newFileName));
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error reading image " + path + ".", e);
            }
        }
    }

    /**
     * Get the filename of a path, aka. remove the last characters after the '.' character.
     * 
     * @param path the path
     * @return String only containing the filename.
     */
    private String getFileName(final Path path) {
        String filename = path.getFileName().toString();
        int indexOfExtension = filename.lastIndexOf('.');
        return filename.substring(0, indexOfExtension);
    }

    /**
     * Get the extension of a path, aka. remove characters before the '.' character.
     * 
     * @param path the path
     * @return String only containing the extension.
     */
    private String getExtension(final Path path) {
        String filename = path.getFileName().toString();
        int index = filename.lastIndexOf('.');
        return filename.substring(index + 1);
    }

    /**
     * Makes the current image look gray scale (though still represented as RGB).
     * 
     * @throws IOException
     */
    private BufferedImage gray(final BufferedImage image) throws IOException {
        // Nested loop over every pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Get current color; set each channel to luminosity
                Color color = new Color(image.getRGB(x, y));
                int gray = luminosity(color.getRed(), color.getGreen(), color.getBlue());
                // Put new color
                Color newColor = new Color(gray, gray, gray);
                image.setRGB(x, y, newColor.getRGB());
            }
        }
        return image;
    }

    /**
     * Computes the luminosity of an rgb value by one standard formula.
     *
     * @param r red value (0-255)
     * @param g green value (0-255)
     * @param b blue value (0-255)
     * @return luminosity (0-255)
     */
    private int luminosity(int r, int g, int b) {
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }

    /**
     * Flips the image horizontally (left right).
     * 
     * @param image
     * @return
     * @throws IOException
     */
    private BufferedImage flipHorizontally(final BufferedImage image) throws IOException {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /**
     * Flips the image vertically (upside down).
     * 
     * @param image
     * @return
     * @throws IOException
     */
    private BufferedImage flipVertically(final BufferedImage image) throws IOException {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /**
     * 
     * @param image
     * @param rows
     * @param cols
     * @return
     * @throws IOException
     */
    private BufferedImage puzzle(final BufferedImage image, final int cols, final int rows) throws IOException {
        int chunks = rows * cols;
        int count = 0;

        // determines the chunk width and height
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        // Image array to hold image chunks
        BufferedImage[] imageCells = new BufferedImage[chunks];
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                // Initialize the image array with image chunks
                imageCells[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                // draws the image chunk
                Graphics2D gr = imageCells[count].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * x, chunkHeight * y, chunkWidth * x + chunkWidth, chunkHeight * y + chunkHeight, null);
                gr.dispose();
                count++;
            }
        }

        // Randomize cells...
        final long numberOfSwaps = (long) (count * (count / Math.PI));
        for (int i = 0; i < numberOfSwaps; i++) {
            int x1 = randInt(count - 1);
            int x2 = randInt(count - 1);
            BufferedImage temp = imageCells[x1];
            imageCells[x1] = imageCells[x2];
            imageCells[x2] = temp;
        }

        // Initializing the final image
        BufferedImage finalImage = new BufferedImage(chunkWidth * cols, chunkHeight * rows, image.getType());
        int num = 0;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                finalImage.createGraphics().drawImage(imageCells[num++], chunkWidth * x, chunkHeight * y, null);
            }
        }

        return finalImage;
    }

    /**
     * Construct and provide GNU-compatible Options.
     * 
     * @return Options expected from command-line of GNU form.
     */
    private Options constructGnuOptions() {
        final Options gnuOptions = new Options();
        gnuOptions.addOption(Constants.OPTIONS_FLIP_VERTICALLY[0], Constants.OPTIONS_FLIP_VERTICALLY[1], false, Constants.HELP_FLIP_VERTICALLY);
        gnuOptions.addOption(Constants.OPTIONS_FLIP_HORIZONTALLY[0], Constants.OPTIONS_FLIP_HORIZONTALLY[1], false, Constants.HELP_FLIP_HORIZONTALLY);
        gnuOptions.addOption(Constants.OPTIONS_GREY[0], Constants.OPTIONS_GREY[1], false, Constants.HELP_GRAY);
        gnuOptions.addOption(Constants.OPTIONS_IMAGE[0], Constants.OPTIONS_IMAGE[1], true, Constants.HELP_IMAGE);
        gnuOptions.addOption(Constants.OPTIONS_PUZZLE[0], Constants.OPTIONS_PUZZLE[1], true, Constants.HELP_PUZZLE);
        gnuOptions.addOption(Constants.OPTIONS_VERSION[0], Constants.OPTIONS_VERSION[1], false, Constants.HELP_VERSION);
        return gnuOptions;
    }

    /**
     * Write "help" to the provided OutputStream.
     * 
     * @param options the Options
     * @param printedRowWidth the row width
     * @param header the header
     * @param footer the footer
     * @param spacesBeforeOption the spaces before options
     * @param spacesBeforeOptionDescription the spaces before options description
     * @param displayUsage the display usage
     */
    @SuppressWarnings("all")
    private void printHelp(final Options options, final int printedRowWidth, final String header, final String footer, final int spacesBeforeOption,
            final int spacesBeforeOptionDescription, final boolean displayUsage) {
        final PrintWriter writer = new PrintWriter(System.out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(writer, printedRowWidth, command, header, options, spacesBeforeOption, spacesBeforeOptionDescription, footer, displayUsage);
        writer.flush();
    }

    /**
     * Checks the command line after the valid options. If not found, returns the default value.
     * 
     * @param validOptions the valid options for choosing option.
     * @param commandLine the commandLine
     * @param defaultValue the default value
     * @return the value from the valid option and if not found the default value.
     */
    private String checkCommandLineForOption(final String[] validOptions, final CommandLine commandLine, final String defaultValue) {
        String returnValue = checkCommandLineForOption(validOptions, commandLine);
        return "".equals(returnValue) ? defaultValue : returnValue;
    }

    /**
     * Checks the command line after options.
     * 
     * @param validOptions the valid options for the option.
     * @param commandLine the commandLine
     * @param delimiter the delimiter to use when splitting the potential values from the option on and from the commandLine.
     * @return String array of values selected field(s) or null if not found in command line.
     */
    private String[] checkCommandLineForOptions(String[] validOptions, CommandLine commandLine, String delimiter) {
        String[] returnValue = null;
        String optionValue = checkCommandLineForOption(validOptions, commandLine, null);
        if (optionValue != null) {
            returnValue = optionValue.split(delimiter);
        }
        return returnValue;
    }

    /**
     * Checks the command line after the valid options.
     * 
     * @param validOptions the valid options for choosing option.
     * @param commandLine the commandLine
     * @return the value from the valid option, if not found returns empty string "".
     */
    private String checkCommandLineForOption(String[] validOptions, CommandLine commandLine) {
        String returnValue = "";
        for (String option : validOptions) {
            if (commandLine.hasOption(option)) {
                returnValue = commandLine.getOptionValue(option);
            }
        }
        return returnValue;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive. The difference between min and max can be at most <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    private int randInt(final int min, final int max) {
        // nextInt is normally exclusive of the top value, so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Returns a pseudo-random number between zero (0) and max, inclusive. Parameter max can be at most <code>Integer.MAX_VALUE - 1</code>.
     * 
     * @param max Maximum value. Must be greater than zero (0).
     * @return Integer between zero (0) and max, inclusive.
     * @see #randInt(int, int)
     */
    private int randInt(final int max) {
        return randInt(0, max);
    }

    /**
     * @return the application
     */
    public String getApplication() {
        return application;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

}
