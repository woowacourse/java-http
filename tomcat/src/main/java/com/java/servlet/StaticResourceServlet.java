package com.java.servlet;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.java.http.HttpRequest.HttpMethod.GET;

public abstract class StaticResourceServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals(resourcePath());
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (resourcePath().endsWith(".html")) {
            return HttpResponse.ok().html(file()).build();
        } else if (resourcePath().endsWith(".css")) {
            return HttpResponse.ok().css(file()).build();
        } else if (resourcePath().endsWith(".js")) {
            return HttpResponse.ok().js(file()).build();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private String file() {
        final var resource = getClass().getClassLoader().getResource("static" + resourcePath());
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract String resourcePath();
}
