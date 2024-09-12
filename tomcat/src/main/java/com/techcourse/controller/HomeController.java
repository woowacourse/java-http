package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends Controller {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setBodyWithContentType("Hello world!", "text/html;charset=utf-8");
    }
}
