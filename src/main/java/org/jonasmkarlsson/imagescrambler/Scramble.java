package org.jonasmkarlsson.imagescrambler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import javax.imageio.ImageIO;

public class Scramble {
    private Random rand = new Random();

    public Scramble() {
        super();
    }

    /**
     * Scramble the image accordingly to parameters
     * 
     * @param path the path to the image to scramble.
     * @param flipV if flip vertical should be done.
     * @param flipH if flip horizontal should be done.
     * @param grey if making the image grey should be done.
     * @param puzzle if making a puzzle of the image should be done.
     * @param numberOfColumns number of columns for puzzle.
     * @param numberOfRows number of rows for puzzle.
     * @throws IOException
     */
    public BufferedImage scramble(final Path path, final boolean flipV, final boolean flipH, final boolean grey, final boolean puzzle, final int numberOfColumns,
            final int numberOfRows) throws IOException {
        BufferedImage image = ImageIO.read(path.toFile());

        if (flipV) {
            image = flipVertically(image);
        }
        if (flipH) {
            image = flipHorizontally(image);
        }
        if (grey) {
            image = gray(image);
        }
        if (puzzle) {
            image = puzzleAndShuffle(image, numberOfColumns, numberOfRows);
        }

        return image;
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
     * Flips the image horizontally (left right).
     * 
     * @param image the image to flip.
     * @return a fliped image.
     * @throws IOException if image is not found.
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
     * @param image the image to flip.
     * @return a fliped image.
     * @throws IOException if image is not found.
     */
    private BufferedImage flipVertically(final BufferedImage image) throws IOException {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /**
     * Divide(s) the image in the column(s) and row(s) => cells, aka. making a puzzle of the image. Then shuffled the cells between each other.
     * 
     * @param image the image to puzzle.
     * @param rows the number of columns to use.
     * @param cols the number of rows to use.
     * @return a puzzled and shuffled image.
     * @throws IOException if image is not found.
     */
    private BufferedImage puzzleAndShuffle(final BufferedImage image, final int cols, final int rows) throws IOException {
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
     * Returns a pseudo-random number between min and max, inclusive. The difference between min and max can be at most <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min the minimum value
     * @param max the maximum value. Must be greater than minimum value.
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
}
