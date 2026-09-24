package com.techcourse.controller;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceRenderer {

    private static final String STATIC_PREFIX = "static";
    private static final String HTML_SUFFIX = ".html";
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    public void render(final String path, final HttpResponse response) throws IOException {
        final String resourcePath = toResourcePath(path);
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            renderNotFound(response);
            return;
        }
        response.setBody(getContentType(resourcePath), readResource(resource));
    }

    private void renderNotFound(final HttpResponse response) throws IOException {
        final URL notFound = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setBody(DEFAULT_CONTENT_TYPE, readResource(notFound));
    }

    private String toResourcePath(final String path) {
        if (path.contains(".")) {
            return STATIC_PREFIX + path;
        }
        return STATIC_PREFIX + path + HTML_SUFFIX;
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript";
        }
        return DEFAULT_CONTENT_TYPE;
    }

    private String readResource(final URL resource) throws IOException {
        return Files.readString(Path.of(resource.getFile()));
    }
}