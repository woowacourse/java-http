package org.apache.coyote.http11.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader {

    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String STATIC_DIRECTORY_PATH = "static";

    private FileReader() {
    }

    public static String readFile(String fileName) {
        String filePath = FileReader.class.getClassLoader().getResource(STATIC_DIRECTORY_PATH + fileName).getPath();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | UncheckedIOException e) {
            return DEFAULT_MESSAGE;
        }
    }
}
