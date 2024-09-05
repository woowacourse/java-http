package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setContentType(request);
        response.setHttpResponseBody(request.getUrlPath());
    }
}
