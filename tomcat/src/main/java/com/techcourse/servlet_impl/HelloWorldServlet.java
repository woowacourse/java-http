package com.techcourse.servlet_impl;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.servlet.Servlet;

import static com.java.http.HttpRequest.HttpMethod.GET;

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
