package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class StaticResourceHandler {

    private final ClassLoader classLoader;

    StaticResourceHandler() {
        this(StaticResourceHandler.class.getClassLoader());
    }

    StaticResourceHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    void serve(String path, HttpResponse response) throws IOException {
        if (!isResourcePath(path)) {
            fillError(HttpStatus.NOT_FOUND, response);
            return;
        }
        try (InputStream resource = classLoader.getResourceAsStream("static" + path)) {
            if (resource == null) {
                fillError(HttpStatus.NOT_FOUND, response);
                return;
            }
            response.setContent(HttpStatus.OK, resource.readAllBytes(), contentType(path));
        }
    }

    void fillError(HttpStatus status, HttpResponse response) {
        byte[] body = status.getReasonPhrase().getBytes(StandardCharsets.UTF_8);
        response.setContent(status, body, "text/plain;charset=utf-8");

        try (InputStream resource = classLoader.getResourceAsStream("static/" + status.getCode() + ".html")) {
            if (resource != null) {
                response.setContent(status, resource.readAllBytes(), "text/html;charset=utf-8");
            }
        } catch (IOException ignored) {
        }
    }

    private boolean isResourcePath(String path) {
        if (!path.startsWith("/") || path.endsWith("/") || path.contains("\\") || path.indexOf('\0') >= 0) {
            return false;
        }
        return Arrays.stream(path.split("/"))
                .noneMatch(segment -> segment.equals("..") || segment.equals("."));
    }

    private String contentType(String path) {
        String contentType = URLConnection.guessContentTypeFromName(path);
        if (contentType == null) {
            return "application/octet-stream";
        }
        if (contentType.equals("text/html")) {
            return "text/html;charset=utf-8";
        }
        return contentType;
    }
}
