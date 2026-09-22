package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        if ("GET".equals(request.method())) {
            return doGet(request);
        }
        if ("POST".equals(request.method())) {
            return doPost(request);
        }
        return HttpResponse.error(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.error(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected HttpResponse doPost(final HttpRequest request) {
        return HttpResponse.error(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
