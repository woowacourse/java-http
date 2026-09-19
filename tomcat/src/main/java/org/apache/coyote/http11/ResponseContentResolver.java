package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

class ResponseContentResolver {

    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String JAVASCRIPT_CONTENT_TYPE = "text/javascript;charset=utf-8";

    private static final Map<String, StaticResource> RESOURCES = Map.of(
            "/index.html", new StaticResource("/index.html", HTML_CONTENT_TYPE),
            "/login", new StaticResource("/login.html", HTML_CONTENT_TYPE),
            "/css/styles.css", new StaticResource("/css/styles.css", CSS_CONTENT_TYPE),
            "/js/scripts.js", new StaticResource("/js/scripts.js", JAVASCRIPT_CONTENT_TYPE),
            "/assets/chart-area.js", new StaticResource("/assets/chart-area.js", JAVASCRIPT_CONTENT_TYPE),
            "/assets/chart-bar.js", new StaticResource("/assets/chart-bar.js", JAVASCRIPT_CONTENT_TYPE),
            "/assets/chart-pie.js", new StaticResource("/assets/chart-pie.js", JAVASCRIPT_CONTENT_TYPE));

    ResponseContent resolve(final String path) throws IOException {
        final var resource = RESOURCES.get(path);
        if (resource == null) {
            return new ResponseContent(HTML_CONTENT_TYPE, "Hello world!".getBytes(StandardCharsets.UTF_8));
        }
        return new ResponseContent(resource.contentType(), readResource(resource.path()));
    }

    private byte[] readResource(final String path) throws IOException {
        final var resourcePath = "static" + path;
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException(resourcePath + " not found");
            }
            return inputStream.readAllBytes();
        }
    }

    private record StaticResource(String path, String contentType) {
    }
}
