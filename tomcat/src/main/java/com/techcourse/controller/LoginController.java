package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class LoginController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        User user = LoginService.login(request.findQuery("account"), request.findQuery("password"));
        response.setView(ViewResolver.getView("login.html"));
        response.setStatus(HttpStatus.FOUND);
        response.setHeaders(HttpHeaders.of(request, response));
        log.info("[Login Success] = {}", user);
    }
}
