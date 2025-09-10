package org.apache.coyote.http11.handler.statics.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;

public final class StaticResourceUtils {

    private static final String BASE_PATH = "static/";

    private StaticResourceUtils() {
    }

    public static void serve(HttpResponse response, String filePath, HttpStatus status) throws IOException {
        String resourcePath = BASE_PATH + filePath;

        try (InputStream inputStream = StaticResourceUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                response.status(HttpStatus.NOT_FOUND.getCode(), HttpStatus.NOT_FOUND.getReason())
                        .contentType("text/plain;charset=utf-8")
                        .write(("Not Found: " + resourcePath).getBytes(StandardCharsets.UTF_8));
                return;
            }

            byte[] bytes = inputStream.readAllBytes();
            String contentType = resolveContentType(filePath);

            response.status(status.getCode(), status.getReason())
                    .contentType(contentType)
                    .write(bytes);
        }
    }

    private static String resolveContentType(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".html")) return "text/html;charset=utf-8";
        if (lower.endsWith(".css")) return "text/css;charset=utf-8";
        if (lower.endsWith(".js")) return "application/javascript;charset=utf-8";
        if (lower.endsWith(".json")) return "application/json;charset=utf-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
