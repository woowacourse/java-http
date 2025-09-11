package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        final var method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            return doGet(request);
        }
        if ("POST".equalsIgnoreCase(method)) {
            return doPost(request);
        }

        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(405, "Method Not Allowed")
                .contentType("text/plain;charset=utf-8")
                .body("Method Not Allowed".getBytes())
                .build();
    }

    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(405, "Method Not Allowed")
                .contentType("text/plain;charset=utf-8")
                .body("Method Not Allowed".getBytes())
                .build();
    }

    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(405, "Method Not Allowed")
                .contentType("text/plain;charset=utf-8")
                .body("Method Not Allowed".getBytes())
                .build();
    }
}
