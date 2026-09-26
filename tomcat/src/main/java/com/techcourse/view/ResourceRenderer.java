package com.techcourse.view;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ResourceRenderer {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";

    public void render(final String view, final HttpResponse response) throws IOException {
        writeResource(response, STATIC_RESOURCE_PATH + appendHtmlExtension(view));
    }

    private void writeResource(final HttpResponse response, final String resourcePath) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                writeResource(response, NOT_FOUND_RESOURCE_PATH);
                return;
            }

            String contentType = MimeTypeResolver.resolve(resourcePath);
            if (contentType.startsWith("text/")) {
                contentType += ";charset=utf-8";
            }
            response.setHeader("Content-Type", contentType);
            response.setBody(resource.readAllBytes());
        }
    }

    private static String appendHtmlExtension(final String resourcePath) {
        final var fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }
}
