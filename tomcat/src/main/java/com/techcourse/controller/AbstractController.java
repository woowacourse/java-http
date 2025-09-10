package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.getMethod().equals("GET")) {
            return doGet(request);
        }
        if (request.getMethod().equals("POST")) {
            return doPost(request);
        }
        return getMethodNotAllowedResponse();
    }

    protected HttpResponse doPost(HttpRequest request) {
        return getMethodNotAllowedResponse();
    }

    protected HttpResponse doGet(HttpRequest request) {
        return getMethodNotAllowedResponse();
    }

    private HttpResponse getMethodNotAllowedResponse() {
        return new HttpResponse(
                HttpStatusCode.METHOD_NOT_ALLOWED,
                ContentType.TEXT,
                "Method not allowed"
        );
    }
}
