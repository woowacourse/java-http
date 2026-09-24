package com.techcourse.web.resource;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceHandler {

    public void serve(String pathUri, HttpResponse response) throws IOException {
        Path path = getPath("static" + normalizePathUri(pathUri));

        if (path == null) {
            Path notFoundPath = getPath("static/404.html");

            response.status(HttpStatus.NOT_FOUND)
                    .contentType("html")
                    .body(findResponseBody("/404.html", notFoundPath));
            return;
        }

        response.contentType(extractType(path))
                .body(findResponseBody(pathUri, path));
    }

    private Path getPath(String filePath) throws IOException {
        URL resource = getClass().getClassLoader().getResource(filePath);

        if (resource == null) {
            return null;
        }

        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IOException(
                    "정적 리소스 경로를 변환할 수 없습니다: " + filePath,
                    e
            );
        }
    }

    private String normalizePathUri(String pathUri) {
        if (pathUri.equals("/")) {
            return "/";
        }
        if (!pathUri.contains(".")) {
            return pathUri + ".html";
        }
        return pathUri;
    }

    private String findResponseBody(String pathUri, Path path) throws IOException {
        if (pathUri.equals("/")) {
            return "Hello world!";
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private String extractType(Path path) {
        if (path.toString().endsWith(".css")) {
            return "css";
        }
        if (path.toString().endsWith(".js")) {
            return "javascript";
        }
        return "html";
    }
}
