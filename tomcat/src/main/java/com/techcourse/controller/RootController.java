package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String body = "Hello world!";

        response.setStatusCode(StatusCode.OK);
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html" + HttpHeaders.CHARSET_UTF_8);
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.setBody(body);
    }
}
