package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.ok("text/html", "Hello world!");
    }
}
