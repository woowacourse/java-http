package org.apache.catalina.handle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Optional;
import org.apache.coyote.http11.data.HttpResponse;

public class ViewResolver {

    private static final String REDIRECT_PREFIX = "redirect:";

    private final String path;
    private final Charset charset;

    public ViewResolver(String path, Charset charset) {
        this.path = path;
        this.charset = charset;
    }

    public HttpResponse resolve(HttpResponse response) {
        final var viewName = response.getViewName();
        if (viewName.isEmpty()) {
            return response;
        }

        if (viewName.startsWith(REDIRECT_PREFIX)) {
            renderRedirect(viewName, response);
            response.setViewName(null);
            return response;
        }

        renderStaticView(viewName, response);
        response.setViewName(null);
        return response;
    }

    private void renderRedirect(String viewName, HttpResponse response) {
        response.setStatusCode(302);
        response.setHeader("Location", viewName.substring(REDIRECT_PREFIX.length()));
        response.setBody("");
    }

    private void renderStaticView(String viewName, HttpResponse response) {
        final String resourcePath = resolveResourcePath(viewName);
        final Optional<String> body = readResource(resourcePath);
        if (body.isEmpty()) {
            renderNotFound(response);
            return;
        }

        response.setBody(body.get());
        response.setHeader("Content-Type",
                getContentType(resourcePath) + ";charset=" + charset.name().toLowerCase(Locale.ROOT));
    }

    private String resolveResourcePath(String viewName) {
        return path + (viewName.startsWith("/") ? viewName : "/" + viewName);
    }

    private Optional<String> readResource(String resourcePath) {
        try (var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }

            return Optional.of(new String(resourceStream.readAllBytes(), charset));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read view: " + resourcePath, e);
        }
    }

    private void renderNotFound(HttpResponse response) {
        response.setStatusCode(404);
        response.setBody("Not Found");
    }

    private String getContentType(String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }
        throw new IllegalArgumentException("Unsupported resource type: " + resourcePath);
    }
}
