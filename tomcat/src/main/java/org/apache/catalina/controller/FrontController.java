package org.apache.catalina.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class FrontController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        final String method = httpRequest.getRequestLine().getMethod();

        return switch (method) {
            case "GET" -> doGet(httpRequest);
            case "POST" -> doPost(httpRequest);
            default -> throw new UnsupportedOperationException("HTTP method not supported: " + method);
        };
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException("GET method not implemented");
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException("POST method not implemented");
    }
}
