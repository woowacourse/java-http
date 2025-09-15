package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    public boolean supports(final HttpRequest request) {
        return request.getRequestLine().getMethod().equals("GET") &&
               request.getRequestLine().getPath().equals("/");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        byte[] body = "Hello world!".getBytes();

        response.setStatusLine("HTTP/1.1 200 OK");
        response.setBody(body);
    }
}
