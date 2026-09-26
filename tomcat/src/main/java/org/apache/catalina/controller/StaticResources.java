package org.apache.catalina.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.requestline.RequestPath;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class StaticResources {
    private static final String STATIC_DIRECTORY = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final byte[] NOT_FOUND_FALLBACK = "Not Found".getBytes(StandardCharsets.UTF_8);

    private StaticResources() {
    }

    public static void serve(final String path, final HttpResponse response) throws IOException, URISyntaxException {
        final Optional<Path> found = find(path);
        if (found.isEmpty()) {
            sendNotFound(response);
            return;
        }
        response.setContentType(ContentType.from(path));
        response.setBody(Files.readAllBytes(found.get()));
    }

    private static void sendNotFound(final HttpResponse response) throws IOException, URISyntaxException {
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(ContentType.HTML);
        final Optional<Path> notFoundPage = find(NOT_FOUND_PAGE);
        response.setBody(notFoundPage.isPresent() ? Files.readAllBytes(notFoundPage.get()) : NOT_FOUND_FALLBACK);
    }

    private static Optional<Path> find(final String path) throws URISyntaxException {
        final URL resource = StaticResources.class.getClassLoader().getResource(STATIC_DIRECTORY + path);
        if (resource == null) {
            return Optional.empty();
        }
        final Path file = Path.of(resource.toURI());
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        return Optional.of(file);
    }
}
