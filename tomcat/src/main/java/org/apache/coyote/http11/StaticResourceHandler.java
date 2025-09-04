package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticResourceHandler {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    public static String getResource(String resourcePath) {
        if(Objects.equals(resourcePath, "/")) {
            return "Hello world!";
        }
        final var resource = ClassLoader.getSystemResource(getResolvedPath(resourcePath));

        if (resource == null) {
            return "Not found: " + resourcePath;
        }
        try {
            final var path = Paths.get(resource.getPath());
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getResolvedPath(final String resourcePath) {
        if (resourcePath.contains(".")) {
            return "static" + resourcePath;
        }
        return "static" + resourcePath + ".html";
    }

    public static String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return DEFAULT_CONTENT_TYPE;
    }
}
