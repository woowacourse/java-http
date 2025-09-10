package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticResourceHandler {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String PREFIX = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String EXTENSION_DELIMITER = ".";

    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
    private static final String APPLICATION_JAVASCRIPT_CHARSET_UTF_8 = "application/javascript;charset=utf-8";

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
        if (resourcePath.contains(EXTENSION_DELIMITER)) {
            return PREFIX + resourcePath;
        }
        return PREFIX + resourcePath + DEFAULT_EXTENSION;
    }

    public static String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return TEXT_HTML_CHARSET_UTF_8;
        }
        if (resourcePath.endsWith(".css")) {
            return TEXT_CSS_CHARSET_UTF_8;
        }
        if (resourcePath.endsWith(".js")) {
            return APPLICATION_JAVASCRIPT_CHARSET_UTF_8;
        }
        return DEFAULT_CONTENT_TYPE;
    }
}
