package com.techcourse.controller;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setBody("Hello world!");
    }
}
