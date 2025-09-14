package org.apache.coyote.http11.controller;

import ch.qos.logback.core.util.FileUtil;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.StatusCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StaticResourceHandler {

    private static final String STATIC_PREFIX = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String EXTENSION_DELIMITER = ".";
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    public void execute(HttpRequest request, HttpResponse response) {
        setContentType(request, response);
        setBody(request, response);
    }

    private void setContentType(final HttpRequest request, final HttpResponse response) {
        if (request.getPath().endsWith(".html")) {
            response.setContentType("text/html;charset=utf-8");
            return;
        }
        if (request.getPath().endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
            return;
        }
        if (request.getPath().endsWith(".js")) {
            response.setContentType("application/javascript;charset=utf-8");
            return;
        }
        response.setContentType(DEFAULT_CONTENT_TYPE);
    }

    private void setBody(final HttpRequest request, final HttpResponse response) {
        if (Objects.equals(request.getPath(), "/")) {
            response.setStatusCode(StatusCode.OK);
            response.setBodyAndContentLength("Hello world!");
            return;
        }

        String body = getStaticResource(request.getPath());
        response.setStatusCode(StatusCode.OK);

        if (body == null) {
            response.setStatusCode(StatusCode.NOT_FOUND);
            body = getStaticResource("/404.html");
        }
        response.setBodyAndContentLength(Objects.requireNonNull(body));
    }

    public static String getStaticResource(final String resourcePath) {
        final var wholeResourcePath = getWholeResourcePath(resourcePath);

        try (final var inputStream = FileUtil.class.getClassLoader().getResourceAsStream(wholeResourcePath)) {
            if (inputStream == null) {
                return null;
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getWholeResourcePath(final String resourcePathPart) {
        if (resourcePathPart.contains(EXTENSION_DELIMITER)) {
            return STATIC_PREFIX + resourcePathPart;
        }
        return STATIC_PREFIX + resourcePathPart + DEFAULT_EXTENSION;
    }
}
