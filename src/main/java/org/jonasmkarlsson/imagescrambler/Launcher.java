package org.jonasmkarlsson.imagescrambler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * Scrambles an image
 * 
 * @author jonasmkarlsson
 *
 */
public class Launcher {
    private static final Logger LOGGER = Logger.getLogger(Launcher.class);

    private final Properties properties;
    private final String application;
    private final String command;

    public Launcher() {
        super();
        properties = readPropertiesFile(Constants.PROPERTY_FILE);
        application = properties.getProperty(Constants.PROPERTY_APPLICATION);
        command = properties.getProperty(Constants.PROPERTY_COMMAND);
    }

    /**
     * Read the properties file for the application.
     * 
     * @return
     */
    private Properties readPropertiesFile(final String name) {
        Properties prop = new Properties();
        InputStream stream = this.getClass().getResourceAsStream(name);
        try {
            prop.load(stream);
        } catch (IOException e) {
            LOGGER.error("Encountered exception while reading property file '" + name + "'. Some features might not work correctley.");
            LOGGER.error("Exception: " + e);
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
            System.out.println("Encountered exception while parsing using GnuParser: " + parseException.getMessage());
            LOGGER.error("Encountered exception while parsing using GnuParser:", parseException);
        } catch (IOException ioException) {
            System.out.println("Encountered IO exception while trying to read file: " + ioException.getMessage());
            LOGGER.error("Encountered IO exception while trying to read file: ", ioException);
        }
    }

    /**
     * Parse the command line and execute the action.
     * 
     * @param commandLine the commandLine
     * @throws IOException if image file can't not be found.
     */
    private void useGnuParser(final CommandLine commandLine) throws IOException {
        // Find each parameter...
        boolean directory = commandLine.hasOption(Constants.OPTIONS_DIRECTORY[0]) || commandLine.hasOption(Constants.OPTIONS_DIRECTORY[1]);
        boolean flipV = commandLine.hasOption(Constants.OPTIONS_FLIP_VERTICALLY[0]) || commandLine.hasOption(Constants.OPTIONS_FLIP_VERTICALLY[1]);
        boolean flipH = commandLine.hasOption(Constants.OPTIONS_FLIP_HORIZONTALLY[0]) || commandLine.hasOption(Constants.OPTIONS_FLIP_HORIZONTALLY[1]);
        boolean grey = commandLine.hasOption(Constants.OPTIONS_GREY[0]) || commandLine.hasOption(Constants.OPTIONS_GREY[1]);
        boolean recursive = commandLine.hasOption(Constants.OPTIONS_RECURSIVE[0]) || commandLine.hasOption(Constants.OPTIONS_RECURSIVE[1]);
        boolean pattern = commandLine.hasOption(Constants.OPTIONS_PATTERN[0]) || commandLine.hasOption(Constants.OPTIONS_PATTERN[1]);
        boolean puzzle = commandLine.hasOption(Constants.OPTIONS_PUZZLE[0]) || commandLine.hasOption(Constants.OPTIONS_PUZZLE[1]);
        boolean prune = commandLine.hasOption(Constants.OPTIONS_PRUNE[0]) || commandLine.hasOption(Constants.OPTIONS_PRUNE[1]);

        int numberOfColumns = Constants.DEFAULT_PUZZLE_COLUMNS_ROWS;
        int numberOfRows = Constants.DEFAULT_PUZZLE_COLUMNS_ROWS;

        // Get and set options that are required
        String outputDirectoryValue = getOptionValue(Constants.OPTIONS_OUTPUT_DIRECTORY, commandLine, Constants.DEFAULT_DIRECTORY);

        if (puzzle) {
            String defaultNumberOfColumnsAndRows = numberOfColumns + "," + numberOfRows;
            String numberOfColumnsAndRows = getOptionValue(Constants.OPTIONS_PUZZLE, commandLine, defaultNumberOfColumnsAndRows);
            String[] columnsAndRows = numberOfColumnsAndRows.split(",");
            numberOfColumns = Integer.valueOf(columnsAndRows[0]);
            numberOfRows = Integer.valueOf(columnsAndRows[1]);
        }

        if (directory) {
            String directoryValue = getOptionValue(Constants.OPTIONS_DIRECTORY, commandLine, Constants.DEFAULT_DIRECTORY);
            String patternValue = getOptionValue(Constants.OPTIONS_PATTERN, commandLine, Constants.DEFAULT_DIRECTORY);
            Path startingPath = Paths.get(directoryValue);

            LOGGER.info("directoryValue: " + directoryValue);
            LOGGER.info("patternValue: " + patternValue);
            LOGGER.info("startingPath = '" + startingPath + "'");
            LOGGER.info("recursive = '" + recursive + "'");
            LOGGER.info("prune = '" + prune + "'");

            FileFinder fileFinder = new FileFinder(patternValue, recursive);
            if (prune) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Prune is set to '" + prune + "', FileWalker are follow links.");
                }
                EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
                Files.walkFileTree(startingPath, opts, Integer.MAX_VALUE, fileFinder);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Prune is set to '" + prune + "', FileWalker are not follow links.");
                }
                Files.walkFileTree(startingPath, fileFinder);
            }

            LOGGER.info("Found " + fileFinder.getMatchedFiles().size() + " file(s).");

            for (Path path : fileFinder.getMatchedFiles()) {
                scrambleAndSaveImage(path, outputDirectoryValue, flipV, flipH, grey, puzzle, numberOfColumns, numberOfRows);
            }

        } else if (pattern) {
            Path path = FileSystems.getDefault().getPath(getOptionValue(Constants.OPTIONS_PATTERN, commandLine, ""));
            scrambleAndSaveImage(path, outputDirectoryValue, flipV, flipH, grey, puzzle, numberOfColumns, numberOfRows);
        } else {
            printOutOptions(commandLine);
        }
    }

    private void scrambleAndSaveImage(final Path path, final String outputDir, final boolean flipV, final boolean flipH, final boolean grey,
            final boolean puzzle, final int numberOfColumns, final int numberOfRows) throws IOException {
        LOGGER.info("Scrambling path: '" + path.toAbsolutePath() + "'");

        // TODO: Check that outputDir folder exists
        File dir = new File(outputDir);

        if (!dir.exists()) {
            LOGGER.info("Creating directory: '" + dir.getName() + "'");
            try {
                dir.mkdir();
                LOGGER.debug("Created directory '" + dir.getAbsolutePath() + "'.");
            } catch (SecurityException se) {
                LOGGER.error(
                        "Encountered exception while trying to create directory '" + dir.getAbsolutePath() + "'. Some features might not work correctley.");
                LOGGER.error("Exception: " + se);
            }
        }

        BufferedImage scrambledImage = new Scramble().scramble(path, flipV, flipH, grey, puzzle, numberOfColumns, numberOfRows);
        String ext = getExtension(path);
        String name = createFileName(path, flipV, flipH, grey, puzzle) + "." + ext;
        File scrambledFile = new File(outputDir + File.separator + name);
        LOGGER.info("Saving scrambled path: '" + scrambledFile.getAbsolutePath() + "'");
        ImageIO.write(scrambledImage, ext, scrambledFile);
    }

    private String createFileName(final Path path, final boolean flipV, final boolean flipH, final boolean grey, final boolean puzzle) {
        String fileName = getFileName(path);
        if (flipV) {
            fileName = fileName + "-" + Constants.OPTIONS_FLIP_VERTICALLY[1];
        }
        if (flipH) {
            fileName = fileName + "-" + Constants.OPTIONS_FLIP_HORIZONTALLY[1];
        }
        if (grey) {
            fileName = fileName + "-" + Constants.OPTIONS_GREY[1];
        }
        if (puzzle) {
            fileName = fileName + "-" + Constants.OPTIONS_PUZZLE[1];
        }
        return fileName;
    }

    /**
     * Get the filename of a path, aka. remove characters after the '.' character.
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
     * 
     * @param commandLine
     */
    @SuppressWarnings("all")
    private void printOutOptions(CommandLine commandLine) {
        if (commandLine.hasOption(Constants.OPTIONS_VERSION[0]) || commandLine.hasOption(Constants.OPTIONS_VERSION[1])) {
            System.out.println(application);
        } else {
            printHelp(constructGnuOptions(), 120, "Help GNU", "End of GNU Help", 5, 3, true);
        }
    }

    /**
     * Construct and provide GNU-compatible Options.
     * 
     * @return Options expected from command-line of GNU form.
     */
    private Options constructGnuOptions() {
        final Options gnuOptions = new Options();
        gnuOptions.addOption(Constants.OPTIONS_DIRECTORY[0], Constants.OPTIONS_DIRECTORY[1], true, Constants.HELP_DIRECTORY);
        gnuOptions.addOption(Constants.OPTIONS_FLIP_VERTICALLY[0], Constants.OPTIONS_FLIP_VERTICALLY[1], false, Constants.HELP_FLIP_VERTICALLY);
        gnuOptions.addOption(Constants.OPTIONS_FLIP_HORIZONTALLY[0], Constants.OPTIONS_FLIP_HORIZONTALLY[1], false, Constants.HELP_FLIP_HORIZONTALLY);
        gnuOptions.addOption(Constants.OPTIONS_GREY[0], Constants.OPTIONS_GREY[1], false, Constants.HELP_GRAY);
        gnuOptions.addOption(Constants.OPTIONS_OUTPUT_DIRECTORY[0], Constants.OPTIONS_OUTPUT_DIRECTORY[1], true, Constants.HELP_OUTPUT_DIRECTORY);
        gnuOptions.addOption(Constants.OPTIONS_PATTERN[0], Constants.OPTIONS_PATTERN[1], true, Constants.HELP_PATTERN);
        gnuOptions.addOption(Constants.OPTIONS_PUZZLE[0], Constants.OPTIONS_PUZZLE[1], true, Constants.HELP_PUZZLE);
        gnuOptions.addOption(Constants.OPTIONS_PUZZLE_COLUMNS[0], Constants.OPTIONS_PUZZLE_COLUMNS[1], true, Constants.HELP_PUZZLE_COLUMNS);
        gnuOptions.addOption(Constants.OPTIONS_PUZZLE_ROWS[0], Constants.OPTIONS_PUZZLE_ROWS[1], true, Constants.HELP_PUZZLE_ROWS);
        gnuOptions.addOption(Constants.OPTIONS_PRUNE[0], Constants.OPTIONS_PRUNE[1], false, Constants.HELP_PRUNE);
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
     * Check and gets an option value from the command line. If option are not found, returns the
     * default value.
     * 
     * @param options the options, valid once.
     * @param commandLine the commandLine
     * @param defaultValue the default value to return if any of the option(s) are not found.
     * @return the option value, if not found, returns the defaultValue.
     * @see #getOptionValue(String[], CommandLine)
     */
    private String getOptionValue(final String[] options, final CommandLine commandLine, final String defaultValue) {
        String returnValue = defaultValue;
        for (String option : options) {
            if (commandLine.hasOption(option)) {
                returnValue = commandLine.getOptionValue(option);
                break;
            }
        }
        return returnValue;
    }

    /**
     * @return the application
     */
    public String getApplication() {
        return application;
    }

}
