package com.techcourse.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = "Hello world!";
        response.setBody(body);
    }
}
