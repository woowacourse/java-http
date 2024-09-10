package com.techcourse.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class StaticResourceManager {
    private static final String CRLF = "\r\n";

    private StaticResourceManager() {
    }

    public static boolean isExist(String filePath) {
        URL resource = ClassLoader.getSystemClassLoader().getResource(filePath);
        return resource != null;
    }

    public static String read(String filePath) {
        URL resource = ClassLoader.getSystemClassLoader().getResource(filePath);

        try {
            Path path = Optional.ofNullable(resource)
                    .map(url -> {
                        try {
                            return url.toURI();
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(Path::of)
                    .orElseThrow(() -> new RuntimeException(filePath + " not found"));

            StringBuilder sb = new StringBuilder();
            Files.readAllLines(path).forEach(line -> sb.append(line).append("\n"));
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
