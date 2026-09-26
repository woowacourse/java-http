package com.techcourse.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ViewResolver {

    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String STATIC_RESOURCE_PATH = "static/";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";

    public void resolve(final String view, final HttpResponse response) throws IOException {
        if (view == null) {
            return;
        }
        if (view.startsWith(REDIRECT_PREFIX)) {
            response.sendRedirect(view.substring(REDIRECT_PREFIX.length()));
            return;
        }

        final String resourcePath = resolveResourcePath(view);
        final Optional<byte[]> body = readResource(resourcePath);
        if (body.isPresent()) {
            writeBody(response, resourcePath, body.get());
            return;
        }

        response.setStatus(HttpStatus.NOT_FOUND);
        final byte[] errorBody = readResource(NOT_FOUND_RESOURCE_PATH).orElseGet(() -> new byte[0]);
        writeBody(response, NOT_FOUND_RESOURCE_PATH, errorBody);
    }

    private Optional<byte[]> readResource(final String resourcePath) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                return Optional.empty();
            }
            return Optional.of(resource.readAllBytes());
        }
    }

    private void writeBody(final HttpResponse response, final String resourcePath, final byte[] body) {
        String contentType = MimeTypeResolver.resolve(resourcePath);
        if (contentType.startsWith("text/")) {
            contentType += ";charset=utf-8";
        }
        response.setHeader("Content-Type", contentType);
        response.setBody(body);
    }

    private static String resolveResourcePath(final String view) {
        String path = view;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return STATIC_RESOURCE_PATH + appendHtmlExtension(path);
    }

    private static String appendHtmlExtension(final String resourcePath) {
        final var fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }
}
