package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceHandler {

    public void serve(final String resourcePath,
                      final HttpResponse response) throws IOException {
        try (InputStream resourceInputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (resourceInputStream == null) {
                response.setStatus(404, "Not Found");
                response.setHeader("Content-Type", "text/plain;charset=utf-8");
                response.setBody("Not Found".getBytes(StandardCharsets.UTF_8));
                return;
            }

            response.setHeader("Content-Type", determineContentType(resourcePath));
            response.setBody(resourceInputStream.readAllBytes());
        }
    }

    private String determineContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }
        if (resourcePath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (resourcePath.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }
}
