package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        return switch (request.getMethod()) {
            case "GET" -> doGet(request);
            case "POST" -> doPost(request);
            default -> methodNotAllowed();
        };
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    private HttpResponse methodNotAllowed() {
        return HttpResponse.create("405 Method Not Allowed", "text/plain", "Method Not Allowed");
    }
}
