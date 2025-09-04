package com.http.application;

import com.http.domain.HttpRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public final class StaticResourceHandler {

    private StaticResourceHandler() {
    }

    public static byte[] getResponseBody(HttpRequest httpRequest) throws IOException {
        final URL url = getFileUrl(httpRequest);

        if (url == null) {
            return "Hello world!".getBytes();
        }

        return Files.readAllBytes(new File(url.getFile()).toPath());
    }

    private static URL getFileUrl(HttpRequest httpRequest) {
        String path = httpRequest.startLine().path();

        if (!path.contains(".")) {
            path += ".html";
        }

        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        resourcePath = "static/" + resourcePath;

        return StaticResourceHandler.class.getClassLoader().getResource(resourcePath);
    }
}
