package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

final class StaticResourceLoader {
    private StaticResourceLoader() {
    }

    static String load(String resourcePath) {
        URL resource = StaticResourceLoader.class.getResource("/static" + resourcePath);
        if (resource == null) {
            return "Hello world!";
        }
        try {
            return Files.readString(Path.of(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            return "Hello world!";
        }
    }
}
