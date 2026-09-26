package com.techcourse.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class RootController extends AbstractController {

    private static final String HTTP_STATUS_OK = "200 OK";
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.send(HTTP_STATUS_OK, CONTENT_TYPE_TEXT_HTML, "Hello world!");
    }
}
