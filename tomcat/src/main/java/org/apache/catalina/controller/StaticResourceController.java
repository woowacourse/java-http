package org.apache.catalina.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

public class StaticResourceController extends MappingController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(Status.OK);
        response.forward(request.getUri());
    }
}
