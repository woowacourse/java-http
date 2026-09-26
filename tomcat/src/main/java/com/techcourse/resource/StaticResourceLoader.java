package com.techcourse.resource;

import com.techcourse.exception.ResourceNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceLoader {

    public StaticResource load(String requestPath)
            throws IOException, URISyntaxException {

        Path path = resolveResourcePath(requestPath);
        byte[] body = Files.readAllBytes(path);
        String contentType = resolveContentType(path.getFileName().toString());

        return new StaticResource(contentType, body);
    }

    private Path resolveResourcePath(String requestPath) throws URISyntaxException {
        String resourcePath = "static" + requestPath;
        if (requestPath.equals("/login")) {
            resourcePath += ".html";
        }
        if (requestPath.equals("/register")) {
            resourcePath += ".html";
        }
        URL resource = getClass()
                        .getClassLoader()
                        .getResource(resourcePath);

        if (resource == null) {
            throw new ResourceNotFoundException(resourcePath);
        }

        return Path.of(resource.toURI());
    }

    private String resolveContentType(final String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }
        if (fileName.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (fileName.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/plain";
    }
}
