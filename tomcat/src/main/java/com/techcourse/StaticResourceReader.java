package com.techcourse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class StaticResourceReader {

    public static Optional<String> read(String resourcePath) throws URISyntaxException, IOException {
        URL fileUrl = StaticResourceReader.class
                .getClassLoader()
                .getResource(resourcePath);
        if (fileUrl == null) {
            return Optional.empty();
        }

        File file = new File(fileUrl.toURI());
        if (!file.isFile()) {
            return Optional.empty();
        }

        return Optional.of(Files.readString(file.toPath(), StandardCharsets.UTF_8));
    }

    private StaticResourceReader() {
    }
}
