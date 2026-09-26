package com.techcourse.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.mvc.controller.AbstractController;

public class HomeController extends AbstractController {

    @Override
    protected String doGet(final HttpRequest request, final HttpResponse response) {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody("Hello world!".getBytes(UTF_8));
        return null;
    }
}
