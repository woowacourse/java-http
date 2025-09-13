package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public void service(RequestLine requestLine, HttpRequest request, HttpResponse response) throws Exception {
        if (requestLine.isGetMethod()) {
            doGet(request, response, requestLine);
            return;
        }

        if (requestLine.isPostMethod()) {
            doPost(request, response);
            return;
        }

        throw new IllegalArgumentException("[ERROR] invalid method type");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response, RequestLine requestLine) throws Exception { /* NOOP */ }
}
