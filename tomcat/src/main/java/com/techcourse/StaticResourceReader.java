package com.techcourse;

import org.apache.coyote.http11.Http11Processor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class StaticResourceReader {

    public static String read(String resourcePath) throws URISyntaxException, IOException {
        URL fileUrl = Http11Processor.class
                .getClassLoader()
                .getResource(resourcePath);
        File file = new File(Objects.requireNonNull(fileUrl).toURI());
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    private StaticResourceReader() {
    }
}
