package com.techcourse.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceParser {

    private static final String DEFAULT_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_PATH = "/404.html";

    public static String parse(final String filePath) throws IOException {
        URL resource = ResourceParser.class.getClassLoader().getResource(DEFAULT_RESOURCE_PATH + filePath);
        if (resource == null) {
            resource = ResourceParser.class.getClassLoader().getResource(DEFAULT_RESOURCE_PATH + NOT_FOUND_PATH);
        }

        final File file = new File(resource.getFile());
        final Path path = file.toPath();

        return new String(Files.readAllBytes(path));
    }
}
