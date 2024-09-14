package com.techcourse.controller;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType("text/html");
        response.setBody(new HttpBody("Hello world!"));
    }
}
