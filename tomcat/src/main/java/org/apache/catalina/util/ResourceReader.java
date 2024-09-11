package org.apache.catalina.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

public class ResourceReader {

    private static final String END_OF_LINE = "";

    private ResourceReader() {

    }

    public static String read(Path path) throws IOException {
        validateFileExists(path);
        List<String> fileLines = Files.readAllLines(path);
        return concatFileLines(fileLines);
    }

    private static void validateFileExists(Path path) throws FileNotFoundException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }
    }

    private static String concatFileLines(List<String> fileLines) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add(END_OF_LINE);
        return joiner.toString();
    }
}
