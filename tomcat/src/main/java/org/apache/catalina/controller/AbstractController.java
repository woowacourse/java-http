package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        if (request.requestLine().method().equals("POST")) {
            return doPost(request);
        }
        if (request.requestLine().method().equals("GET")) {
            return doGet(request);
        }
        return HttpResponse.empty(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected abstract HttpResponse doPost(final HttpRequest request) throws Exception;

    protected abstract HttpResponse doGet(final HttpRequest request) throws Exception;
}
