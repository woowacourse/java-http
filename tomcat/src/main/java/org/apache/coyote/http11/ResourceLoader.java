package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class ResourceLoader {
    private static final String STATIC_ROOT = "static";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String CHARSET = ";charset=utf-8";

    private static final Map<String, String> CONTENT_TYPE = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript"
    );

    private ResourceLoader() {
    }

    public static Optional<String> read(String path) throws URISyntaxException, IOException {
        URL url = ResourceLoader.class.getClassLoader().getResource(STATIC_ROOT + path);
        if (url == null) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(Paths.get(url.toURI()))));
    }

    public static String contentTypeOf(String path) {
        String extension = getExtension(path);
        return CONTENT_TYPE.getOrDefault(extension, DEFAULT_CONTENT_TYPE) + CHARSET;
    }

    private static String getExtension(String resourcePath) {
        int lastIndex = resourcePath.lastIndexOf(".");
        return resourcePath.substring(lastIndex + 1);
    }
}
