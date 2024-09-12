package org.apache.catalina.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

public class HelloWorldController extends MappingController {

    public static final String BODY_MESSAGE = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(Status.OK);
        response.setContentType(ContentType.of(BODY_MESSAGE));
        response.setBody(BODY_MESSAGE);
    }
}
