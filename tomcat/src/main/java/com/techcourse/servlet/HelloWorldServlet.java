package com.techcourse.servlet;

public class HelloWorldServlet implements Servlet {
    @Override
    public boolean canHandle(byte[] input) {
        return true;
    }

    @Override
    public String handle(byte[] input) {
        final var responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
