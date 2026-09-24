package org.apache.catalina.controller;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setError(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setError(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
