package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.exception.HttpException;

public class ResourceView {

    public String findStaticResource(String uri) {
        Path path = toResourcePath(uri);
        return toHttpResponseMessage(HttpStatus.OK, path);
    }

    public String findErrorPage(HttpException e) {
        final var status = e.getStatus();
        if (HttpStatus.NOT_FOUND.equals(status)) {
            Path path = toResourcePath("/404.html");
            return toHttpResponseMessage(HttpStatus.NOT_FOUND, path);
        }
        Path path = toResourcePath("/500.html");
        return toHttpResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, path);
    }

    private Path toResourcePath(String uri) {
        try {
            final var classLoader = getClass().getClassLoader();
            final var url = classLoader.getResource("static" + toDefaultUri(uri));
            final var file = new File(url.getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw HttpException.ofNotFound(e);
        }
    }

    private static String toDefaultUri(String uri) {
        if (uri.equals("/")) {
            return "/index.html";
        }
        if (!uri.contains(".")) {
            return uri + ".html";
        }
        return uri;
    }

    private String toHttpResponseMessage(HttpStatus status, Path path) {
        try {
            String startLine = String.format("HTTP/1.1 %s ", status.toResponse());
            String contentTypeHeader = String.format("Content-Type: %s;charset=utf-8 ", Files.probeContentType(path));
            String responseBody = new String(Files.readAllBytes(path));
            String contentLengthHeader = String.format("Content-Length: %d " + responseBody.getBytes().length);

            return String.join("\r\n", startLine, contentTypeHeader, contentLengthHeader, "", responseBody);
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }
}
