package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public abstract class StaticResourceServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals(uri());
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return switch (type()) {
            case HTML -> HttpResponse.ok().html(file()).build();
            case CSS -> HttpResponse.ok().css(file()).build();
            case JAVASCRIPT -> HttpResponse.ok().js(file()).build();
        };
    }

    private String file() {
        final var resource = getClass().getClassLoader().getResource(resourcePath());
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected enum StaticResourceType {
        HTML,
        CSS,
        JAVASCRIPT,
    }

    protected abstract String uri();

    protected abstract String resourcePath();

    protected abstract StaticResourceType type();
}
