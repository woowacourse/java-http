package com.techcourse.controller;

import org.apache.coyote.handler.AbstractController;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class RootEndPointHandler extends AbstractController {
    private static final RootEndPointHandler INSTANCE = new RootEndPointHandler();

    private RootEndPointHandler() {
    }

    public static RootEndPointHandler getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder()
                .body("Hello world!")
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/plain")
                .build());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponse(HttpResponse.builder()
                .body("Hello world!")
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/plain")
                .build());
    }
}
