package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.copyFrom(HttpResponse.ok("text/html;charset=utf-8", "Hello world!".getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        doGet(request, response);
    }
}
