package org.jonasmkarlsson.imagescrambler;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FileFinder extends SimpleFileVisitor<Path> {

    private static final Logger LOGGER = Logger.getLogger(FileFinder.class);
    private final PathMatcher matcher;
    private List<Path> matchedFiles = new ArrayList<Path>();
    private boolean searchSubFolders = false;
    private boolean firstFolder = true;
    private String pattern = "*";

    /**
     * Default constructor.
     * 
     * @param pattern the pattern to look for.
     */
    public FileFinder(final String pattern, final boolean searchSubFolders) {
        this.pattern = pattern;
        this.searchSubFolders = searchSubFolders;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + this.pattern);
    }

    /**
     * Compares the glob pattern against the file or directory name.
     * 
     * @param file the relative path
     */
    public void find(Path file) {
        firstFolder = false;
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            matchedFiles.add(file);
        }
    }

    /**
     * Invoke the pattern matching method on each file.
     **/
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

    /**
     * Invoke the pattern matching method on each directory.
     **/
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (firstFolder || searchSubFolders) {
            find(dir);
            return CONTINUE;
        } else {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    // /**
    // * Invoke the pattern matching method on each directory.
    // **/
    // @Override
    // public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    // // if (".".equals(dir.toString()) || searchSubFolders) {
    // // find(dir);
    //
    // if (searchSubFolders) {
    // // find(dir);
    // return CONTINUE;
    // } else {
    // return SKIP_SUBTREE;
    // }
    // }

    /**
     * Check for cycle links.
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        if (exc instanceof FileSystemLoopException) {
            LOGGER.error("Cycle detected at '" + file + "'");
        }
        return CONTINUE;
    }

    /**
     * @return the matchedFiles
     */
    public List<Path> getMatchedFiles() {
        return matchedFiles;
    }

    /**
     * @return the searchSubFolders
     */
    public boolean isSearchSubFolders() {
        return searchSubFolders;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

}