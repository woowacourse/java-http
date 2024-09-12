package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class RegisterController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setView(ViewResolver.getView("register.html"));
        response.setStatus(HttpStatus.OK);
        response.setHeaders(HttpHeaders.create(request, response));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RegisterService.addUser(request.findFromBody("account"),
                request.findFromBody("password"),
                request.findFromBody("email"));
        log.info("[REGISTER] {}", request.findFromBody("account"));
        response.setView(ViewResolver.getView("register.html"));
        response.setStatus(HttpStatus.FOUND);
        response.setHeaders(HttpHeaders.create(request, response));
        response.addHeader("Location", "/index.html");
    }
}
