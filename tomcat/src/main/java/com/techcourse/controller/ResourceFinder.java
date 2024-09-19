package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceFinder {
    public static void setStaticResponse(HttpRequest request, HttpResponse response) {
        String location = request.getLocation();
        String fileName = location.equals("/") ? "/index" : location;
        String extension = request.getExtension();
        URL url = ResourceFinder.class.getClassLoader().getResource("static" + fileName + "." + extension);
        response.addHeader("Content-Type", String.format("text/%s;charset=utf-8", getResponseExtension(extension)));

        if (Objects.isNull(url)) {
            url = ResourceFinder.class.getClassLoader().getResource("static" + "/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
        }
        try {
            Path filePath = Path.of(url.toURI());
            String content = Files.readString(filePath);
            response.setBody(content);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getResponseExtension(String extension) {
        return extension.equals("js") ? "javascript" : extension;
    }
}
