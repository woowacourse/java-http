package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class HelloWorldServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/");
    }

    @Override
    public String handle(HttpRequest request) {
        final var responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
