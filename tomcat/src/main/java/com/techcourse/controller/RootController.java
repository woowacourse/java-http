package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RootController extends AbstractController {

    private static final String DEFAULT_HTML_PATH = ".html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String body = "Hello world!";
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(DEFAULT_HTML_PATH, body);
    }
}
