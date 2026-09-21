package org.apache.catalina.controller;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class HelloWorldController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.OK);
        response.setContentType("text/html");
        response.setBody("Hello world!");
    }
}
