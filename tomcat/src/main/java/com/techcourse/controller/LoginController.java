package com.techcourse.controller;

import org.apache.coyote.http11.domain.controller.AbstractController;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.redirect("/login.html").build();
    }

}
