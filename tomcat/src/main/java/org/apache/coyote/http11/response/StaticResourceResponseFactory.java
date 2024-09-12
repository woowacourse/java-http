package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResourceResponseFactory {

    private final String pathWithExtension;

    public StaticResourceResponseFactory(String pathWithExtension) {
        this.pathWithExtension = pathWithExtension;
    }

    public String createContentType() {
        String extension = pathWithExtension.substring(pathWithExtension.lastIndexOf(".") + 1);
        return getContentTypeWithCharset(extension);
    }

    private String getContentTypeWithCharset(String extension) {
        return switch (extension) {
            case "html" -> "text/html;charset=utf-8";
            case "css" -> "text/css;charset=utf-8";
            case "js" -> "text/javascript;charset=utf-8";
            case "svg" -> "image/svg+xml;charset=utf-8";
            default -> "text/plain;charset=utf-8";
        };
    }

    public String createResponseBody() throws IOException {
        Path path = buildPath(pathWithExtension);
        return new String(Files.readAllBytes(path));
    }

    private Path buildPath(String pathWithExtension) {
        String resourcePath = String.format("static%s", pathWithExtension);
        if (getClass().getClassLoader().getResource(resourcePath) == null) {
            resourcePath = "static/404.html";
        }
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        return Paths.get(resource.getPath());
    }
}
