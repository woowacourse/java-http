package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        return serveResource(request);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return serveResource(request);
    }

    HttpResponse serveResource(final HttpRequest request) throws IOException {
        if (request.path().equals("/")) {
            return HttpResponse.resource(HttpStatus.OK, "/", "Hello world!");
        }
        return createResourceResponse(resolveResourcePath(request.path()));
    }

    private HttpResponse createResourceResponse(final String resourcePath) throws IOException {
        InputStream resourceStream = getResourceStream(resourcePath);
        if (resourceStream == null) {
            return createNotFoundResponse();
        }
        return HttpResponse.resource(HttpStatus.OK, resourcePath, readResourceBody(resourceStream));
    }

    private InputStream getResourceStream(final String resourcePath) {
        String path = "static" + resourcePath;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    private String resolveResourcePath(final String path) {
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        if (!fileName.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private HttpResponse createNotFoundResponse() throws IOException {
        String resourcePath = "/404.html";
        return HttpResponse.resource(
                HttpStatus.NOT_FOUND,
                resourcePath,
                readResourceBody(getResourceStream(resourcePath))
        );
    }

    private String readResourceBody(final InputStream resourceStream) throws IOException {
        if (resourceStream == null) {
            return "";
        }
        try (InputStream stream = resourceStream) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
