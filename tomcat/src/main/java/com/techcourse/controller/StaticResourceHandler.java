package com.techcourse.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.MediaType;

public class StaticResourceHandler implements Controller {

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

    private static final String DEFAULT_PATH = "static";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String requestPath = request.getPath();
        String resource = readResource(requestPath);
        response.setStatusCode(HttpStatusCode.OK);
        MediaType mediaType = MediaType.findByExtension(requestPath);
        response.setContentType(mediaType.value());
        response.setBody(new HttpBody(resource));
    }

    private static String readResource(final String path) {
        final var url = CLASS_LOADER.getResource(DEFAULT_PATH + path);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }
        var resourcePath = Path.of(url.getPath());
        try {
            byte[] resourceByte = Files.readAllBytes(resourcePath);
            return new String(resourceByte);
        } catch (IOException e) {
            return "";
        }
    }
}
