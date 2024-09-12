package com.techcourse.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

public class StaticResourceController extends MappingController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(Status.OK);
        response.setBodyUri(request.getUrl());
        response.setContentType(getContentType(request.getUrl()));
    }
}
