package com.techcourse.controller;

import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class LogoutController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        request.cookie("JSESSIONID").ifPresent(SessionManager::remove);
        return HttpResponse.redirect("/index.html");
    }
}
