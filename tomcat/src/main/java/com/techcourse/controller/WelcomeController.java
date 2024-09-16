package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class WelcomeController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        response.addFileBody("/default.html");
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
    }
}
