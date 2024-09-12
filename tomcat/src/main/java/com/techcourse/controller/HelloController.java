package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpResponseBody("Hello world!");
    }
}
