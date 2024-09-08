package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.CookieService;
import com.techcourse.service.LoginService;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class LoginController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasQuery()) {
            handleLogin(request, response);
            return;
        }
        handleLoginPage(request, response);
    }

    private static void handleLogin(HttpRequest request, HttpResponse response) {
        User user = LoginService.login(request.findFromQueryParam("account"),
                request.findFromQueryParam("password"));
        response.setView(ViewResolver.getView("login.html")); // TODO 추후 삭제
        response.setStatus(HttpStatus.FOUND);
        response.setHeaders(HttpHeaders.of(request, response));
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", CookieService.create(user.getAccount()));
        log.info("[Login Success] = {}", user);
    }

    private void handleLoginPage(HttpRequest request, HttpResponse response) {
        response.setView(ViewResolver.getView("login.html"));
        response.setStatus(HttpStatus.OK);
        response.setHeaders(HttpHeaders.of(request, response));
    }
}
