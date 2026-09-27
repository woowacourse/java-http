package com.techcourse.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ViewResolver {

    private static final ViewResolver INSTANCE = new ViewResolver();

    public View resolve(String path) throws IOException {
        String contentType = "text/html;charset=utf-8";
        if (path.equals("/")) {
            return new View("Hello world!", contentType);
        }
        String resourcePath = path;
        if (path.equals("/login") || path.equals("/register")) {
            resourcePath = path + ".html";
        }

        URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/404.html");
        }

        final String filePath = resource.getFile();

        String body = Files.readString(Paths.get(filePath));
        String type = contentType(resourcePath);
        if (!type.equals("html")) {
            contentType = contentType.replace("html", type);
        }
        return new View(body, contentType);
    }

    public static ViewResolver getINSTANCE() {
        return INSTANCE;
    }

    private String contentType(String path) {
        String type = "html";
        if (path.contains(".")) {
            type = List.of(path.split("\\.")).getLast();
        }
        return type;
    }

    private ViewResolver() {
    }

    public record View(
            String body,
            String contentType
    ) {
    }
}
