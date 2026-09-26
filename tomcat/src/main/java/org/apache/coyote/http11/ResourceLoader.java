package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ResourceLoader {
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_CSS = "text/css";
    public static final String CONTENT_TYPE_JS = "application/javascript";

    private static final String ROOT = "static";

    private ResourceLoader() {
    }

    public static String loadResponseBody(String resourcePath) throws IOException, URISyntaxException {
        var resource = ClassLoader.getSystemResource(ROOT + resourcePath);
        Path path = Path.of(resource.toURI());
        return Files.readString(path);
    }

    public static String findContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return CONTENT_TYPE_CSS;
        }

        if (resourcePath.endsWith(".js")) {
            return CONTENT_TYPE_JS;
        }

        return CONTENT_TYPE_HTML;
    }
}
