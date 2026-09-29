package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StaticResourceController extends AbstractController {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private static final Map<String, String> RESOURCE_ALIASES = Map.of(
            "/login", "/login.html",
            "/register", "/register.html"
    );

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", "text/html;charset=utf-8",
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8"
    );

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        render(request.getPath(), response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        render(request.getPath(), response);
    }

    public void render(final String path, final HttpResponse response)
            throws IOException {
        if ("/".equals(path)) {
            renderDefault(response);
            return;
        }

        final String resourcePath =
                "static" + RESOURCE_ALIASES.getOrDefault(path, path);

        final InputStream resource = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (resource == null) {
            renderDefault(response);
            return;
        }

        try (resource) {
            response.addHeader("Content-Type", contentTypeOf(resourcePath));
            response.setBody(resource.readAllBytes());
        }
    }

    private void renderDefault(final HttpResponse response) {
        response.addHeader("Content-Type", DEFAULT_CONTENT_TYPE);
        response.setBody(DEFAULT_BODY.getBytes(StandardCharsets.UTF_8));
    }

    private String contentTypeOf(final String resourcePath) {
        return CONTENT_TYPES.entrySet().stream()
                .filter(entry -> resourcePath.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_CONTENT_TYPE);
    }
}
