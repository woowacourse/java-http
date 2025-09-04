package com.java.servlet;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.java.http.HttpRequest.HttpMethod.GET;

public abstract class StaticResourceServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals(resourcePath());
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (resourcePath().endsWith(".html")) return HttpResponse.ok().html(file()).build();
        if (resourcePath().endsWith(".css")) return HttpResponse.ok().css(file()).build();
        if (resourcePath().endsWith(".js")) return HttpResponse.ok().js(file()).build();
        throw new UnsupportedOperationException();
    }

    private String file() {
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream("static" + resourcePath())) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract String resourcePath();
}
