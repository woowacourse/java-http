package org.apache.catalina.controller.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileFinder {

    private static final String FILE_PATH = "static/";
    private static final String NOT_FOUND_PAGE = "404.html";

    private FileFinder() {
    }

    public static String find(final String file) throws IOException {
        try {
            final ClassLoader classLoader = FileFinder.class.getClassLoader();
            final URL resource = classLoader.getResource(FILE_PATH + file);
            return new String(
                Files.readAllBytes(new File(Objects.requireNonNull(resource).getPath()).toPath()));
        } catch (NullPointerException e) {
            final ClassLoader classLoader = FileFinder.class.getClassLoader();
            final URL resource = classLoader.getResource(FILE_PATH + NOT_FOUND_PAGE);
            return new String(
                Files.readAllBytes(new File(Objects.requireNonNull(resource).getPath()).toPath()));
        }
    }
}
