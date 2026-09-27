package com.techcourse.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceLoader {

    public static String load(String path) throws IOException {
        try (InputStream resource = StaticResourceLoader.class.getClassLoader().getResourceAsStream("static" + path)) {
            if (resource == null) {
                return null;
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private StaticResourceLoader() {}
}
