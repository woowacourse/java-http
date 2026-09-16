package com.techcourse.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.HttpResponse;

public class StaticResourceHandler {

    public HttpResponse createResponse(String responsePath) throws IOException {
        var resource = readResource(responsePath);
        if (resource.isPresent()) {
            return HttpResponse.ok(getContentType(responsePath), resource.get());
        }

        byte[] notFoundBody = readResource("/404.html")
                .orElseGet(() -> "404 Not Found".getBytes(StandardCharsets.UTF_8));

        return HttpResponse.notFound(notFoundBody);
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private Optional<byte[]> readResource(String path) throws IOException {
        try (InputStream resource = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + path)) {

            if (resource == null) {
                return Optional.empty();
            }

            return Optional.of(resource.readAllBytes());
        }
    }
}
