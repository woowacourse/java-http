package com.techcourse.controller;

import com.techcourse.servlet.AbstractController;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String responseBody = "Hello world!";

        response.setResponseBody(responseBody);
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        response.write();
    }
}
