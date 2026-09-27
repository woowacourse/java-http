package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus("200 OK");
    }
}
