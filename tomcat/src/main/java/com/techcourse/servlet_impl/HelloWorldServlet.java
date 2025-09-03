package com.techcourse.servlet_impl;

import com.java.servlet.HttpRequest;
import com.java.servlet.HttpResponse;
import com.java.servlet.Servlet;

import static com.java.servlet.HttpRequest.HttpMethod.GET;

public class HelloWorldServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.ok().html("Hello world!").build();
    }
}
