package org.apache.catalina.resource;

import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class ResourceHandler {

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
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }
        if (path.endsWith(".ico")) {
            return "image/x-icon";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html;charset=utf-8";
    }
}
