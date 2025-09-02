package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class IndexPageServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/index.html");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        final var responseBody = indexHtml();
        return HttpResponse.ok().html(responseBody).build();
    }

    private String indexHtml() {
        final var resource = getClass().getClassLoader().getResource("static/index.html");
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
