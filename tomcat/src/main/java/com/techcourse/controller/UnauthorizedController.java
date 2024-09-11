package com.techcourse.controller;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public class UnauthorizedController extends AbstractController {

    private static final String UNAUTHORIZED_PATH = "/401.html";

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        return HttpResponse.unauthorized(httpRequest)
                .staticResource(UNAUTHORIZED_PATH)
                .build();
    }
}
