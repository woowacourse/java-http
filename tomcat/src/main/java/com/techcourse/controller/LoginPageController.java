package com.techcourse.controller;

import org.apache.catalina.Manager;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginPageController extends AbstractController {

    @Override
    public ForwardResult execute(HttpRequest request, Manager manager) {
        return new ForwardResult(HttpStatusCode.FOUND, "login.html");
    }
}
