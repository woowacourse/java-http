package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.exception.NotFoundException;

public class ResourceResponse {

    private final Path path;
    private final HttpStatus status;

    public ResourceResponse(String uri, HttpStatus status) {
        this.path = toResourcePath(uri);
        this.status = status;
    }

    public ResourceResponse(String uri) {
        this(uri, HttpStatus.OK);
    }

    public static ResourceResponse ofNotFound() {
        return new ResourceResponse("/404.html", HttpStatus.NOT_FOUND);
    }

    private Path toResourcePath(String uri) {
        try {
            final var classLoader = getClass().getClassLoader();
            final var url = classLoader.getResource("static" + toDefaultUri(uri));
            File file = new File(url.getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw new NotFoundException();
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

    public String toHttpResponseMessage() throws IOException {
        String responseBody = new String(Files.readAllBytes(path));
        String contentType = Files.probeContentType(path);
        return String.join("\r\n",
                String.format("HTTP/1.1 %s ", status.toResponse()),
                String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
