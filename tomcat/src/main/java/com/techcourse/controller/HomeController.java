package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.Request;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponses;

public class HomeController extends AbstractController {

    private static final String BODY = "Hello world!";
    private static final String CONTENT_TYPE = "text/html;charset=utf-8";

    @Override
    protected HttpResponse doGet(final Request request) {
        return HttpResponses.ok(BODY, CONTENT_TYPE);
    }
}
