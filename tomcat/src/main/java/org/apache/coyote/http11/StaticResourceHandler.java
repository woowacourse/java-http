package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class StaticResourceHandler {

    private final ClassLoader classLoader;

    StaticResourceHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    HttpResponse respond(String path) throws IOException {
        HttpResponse response = new HttpResponse();
        serve(path, response);
        return response;
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

    HttpResponse error(HttpStatus status) {
        HttpResponse response = new HttpResponse();
        fillError(status, response);
        return response;
    }

    void fillError(HttpStatus status, HttpResponse response) {
        try (InputStream resource = classLoader.getResourceAsStream("static/" + status.getCode() + ".html")) {
            if (resource != null) {
                response.setContent(status, resource.readAllBytes(), "text/html;charset=utf-8");
                return;
            }
        } catch (IOException ignored) {
            // 오류 페이지를 읽지 못해도 같은 상태 코드의 기본 본문을 응답한다.
        }
        byte[] body = status.getReasonPhrase().getBytes(StandardCharsets.UTF_8);
        response.setContent(status, body, "text/plain;charset=utf-8");
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
