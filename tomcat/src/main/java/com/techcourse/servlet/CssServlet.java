package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class CssServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/css/styles.css");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.ok().css(cssFile()).build();
    }

    private String cssFile() {
        final var resource = getClass().getClassLoader().getResource("static/css/styles.css");
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
