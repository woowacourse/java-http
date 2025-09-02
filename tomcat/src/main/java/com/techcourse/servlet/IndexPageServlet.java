package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class IndexPageServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/index.html");
    }

    @Override
    public String handle(HttpRequest request) {
        return "";
    }
}
