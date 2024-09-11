package com.techcourse.controller;

import org.apache.catalina.Manager;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterPageController extends AbstractController {

    @Override
    public ForwardResult execute(HttpRequest request, HttpResponse response) {
        return new ForwardResult(HttpStatusCode.FOUND, "register.html");
    }
}
