package com.techcourse.app.controller;

import com.techcourse.framework.handler.AbstractController;
import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.response.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setRedirect("/index.html");
    }

}
