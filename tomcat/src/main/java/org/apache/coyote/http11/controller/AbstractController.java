package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestLine;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        RequestLine requestLine = request.getRequestLine();
        if ("GET".equals(requestLine.getMethod())) {
            doGet(request, response);
            return;
        }
        if ("POST".equals(requestLine.getMethod())) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("Unsupported HTTP method: " + requestLine.getMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }
}
