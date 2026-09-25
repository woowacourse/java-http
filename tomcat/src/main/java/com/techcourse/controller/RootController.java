package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok("text/html;charset=utf-8", "Hello world!");
    }
}
