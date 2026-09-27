package org.apache.catalina.resource;

import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class ResourceHandler {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "css", "text/css;charset=utf-8",
            "js", "text/javascript;charset=utf-8",
            "ico", "image/x-icon",
            "png", "image/png",
            "svg", "image/svg+xml"
    );

    public void serve(final String path, final HttpResponse response) throws IOException {
        if (!path.startsWith("/") || path.contains("..") || path.contains("\\")) {
            response.setStatus(404, "Not Found");
            return;
        }
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (resource == null) {
                response.setStatus(404, "Not Found");
                return;
            }
            response.setBody(resource.readAllBytes(), contentType(path));
        }
    }

    private String contentType(final String path) {
        final String extension = path.substring(path.lastIndexOf('.') + 1);
        return CONTENT_TYPES.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }
}
