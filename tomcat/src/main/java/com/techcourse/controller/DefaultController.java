package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.getStaticResource(request.getUrl());
    }
}
