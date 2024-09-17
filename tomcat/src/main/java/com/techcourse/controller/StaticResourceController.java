package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends Controller {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String uri = request.getUri();
        response.setBodyWithStaticResource(uri);
    }
}
