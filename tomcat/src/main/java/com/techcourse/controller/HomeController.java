package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.catalina.connector.Request;
import org.apache.catalina.controller.MethodDispatchingController;
import org.apache.coyote.http11.HttpResponse;

public final class HomeController extends MethodDispatchingController {

    @Override
    protected void doGet(final Request request, final HttpResponse response) {
        response.setBody("text/html;charset=utf-8", "Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}
