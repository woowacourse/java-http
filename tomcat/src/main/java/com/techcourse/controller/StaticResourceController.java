package com.techcourse.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

public class StaticResourceController extends MappingController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String body = resourceResolver.resolve(request.getUri());
        response.setStatusLine(Status.OK);
        response.setContentType(ContentType.of(request.getUri()));
        response.setContentLength(body.getBytes().length);
        response.setBody(body);
    }
}
