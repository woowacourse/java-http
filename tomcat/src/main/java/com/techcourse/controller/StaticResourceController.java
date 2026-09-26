package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected String doGet(final HttpRequest request, final HttpResponse response) {
        return request.path();
    }

    @Override
    protected String doPost(final HttpRequest request, final HttpResponse response) {
        return doGet(request, response);
    }
}
