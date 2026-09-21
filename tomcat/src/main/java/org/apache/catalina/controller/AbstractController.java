package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public String doService(HttpRequest request, HttpResponse response) throws IOException {
        return switch (request.getHttpMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> throw new UnsupportedOperationException();
        };
    }

    protected String doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected String doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException();
    }

}
