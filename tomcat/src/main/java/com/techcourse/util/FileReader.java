package com.techcourse.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class FileReader {
    private static final String CRLF = "\r\n";

    private FileReader() {
    }

    public static String read(String filePath) {
        URL resource = FileReader.class.getClassLoader().getResource(filePath);
        Path path = Optional.ofNullable(resource)
                .map(URL::getPath)
                .map(Path::of)
                .orElseThrow(() -> new RuntimeException(filePath + " not found"));

        try {
            List<String> strings = Files.readAllLines(path);
            return String.join(CRLF, strings);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
