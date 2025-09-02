package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class HelloWorldServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        final var responseBody = "Hello world!";
        return HttpResponse.ok().html(responseBody).build();
    }
}
