package com.techcourse.web;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceHandler {

    private static final String STATIC_RESOURCE_PREFIX = "static/";

    public HttpResponse handle(final String uriPath) throws IOException {
        if (uriPath.equals("/")) {
            final Path indexPath = getFilePath("/index.html");
            return new HttpResponse(HttpStatus.OK, indexPath, "Hello world!", null, null);
        }

        final Path filePath = getFilePath(uriPath);
        if (filePath == null || !Files.isRegularFile(filePath)) {
            return notFoundResponse();
        }

        return new HttpResponse(HttpStatus.OK, filePath, Files.readString(filePath), null, null);
    }

    private HttpResponse notFoundResponse() throws IOException {
        final Path filePath = getFilePath("/404.html");
        return new HttpResponse(HttpStatus.NOT_FOUND, filePath, Files.readString(filePath), null, null);
    }

    private Path getFilePath(final String uriPath) {
        final String resourceName = toResourceName(uriPath);
        if (resourceName == null) {
            return null;
        }

        final URL url = getClass().getClassLoader().getResource(resourceName);
        if (url == null) {
            return null;
        }
        return Path.of(url.getPath());
    }

    private String toResourceName(final String uriPath) {
        final String resourcePath = uriPath.startsWith("/") ? uriPath.substring(1) : uriPath;
        final Path normalizedPath = Path.of(resourcePath).normalize();

        if (normalizedPath.isAbsolute() || normalizedPath.startsWith("..")) {
            return null;
        }

        return STATIC_RESOURCE_PREFIX + normalizedPath;
    }
}
