package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.request_response.HttpMethod.GET;
import static org.apache.coyote.http11.request_response.HttpMethod.POST;

import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

abstract class ServletController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.getRequestMethod().equals(GET)) {
            return doGet(request);
        }
        if (request.getRequestMethod().equals(POST)) {
            return doPost(request);
        }
        throw new IllegalArgumentException("지원하지 않는 http method 입니다.");
    }

    protected abstract HttpResponse doGet(HttpRequest request);
    protected abstract HttpResponse doPost(HttpRequest request);
}
