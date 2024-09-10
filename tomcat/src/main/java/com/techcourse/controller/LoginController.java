package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.SessionService;
import com.techcourse.service.LoginService;
import java.util.Optional;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class LoginController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (alreadyLogin(request)) {
            redirectMain(request, response);
            return;
        }
        if (request.hasQuery()) {
            requestLogin(request, response);
            return;
        }
        requestLoginPage(request, response);
    }

    private static boolean alreadyLogin(HttpRequest request) {
        Optional<String> optionalCookie = request.findFromHeader("Cookie");
        return optionalCookie.filter(SessionService::hasSession).isPresent();
    }

    private static void redirectMain(HttpRequest request, HttpResponse response) {
        response.setHeaders(HttpHeaders.create(request, response));
        setRedirectHeaderToMain(response);
    }

    private static void setRedirectHeaderToMain(HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }

    private static void requestLogin(HttpRequest request, HttpResponse response) {
        User user = LoginService.login(request.findFromQueryParam("account"),
                request.findFromQueryParam("password"));
        response.setHeaders(HttpHeaders.create(request, response));
        response.addHeader("Set-Cookie", SessionService.createCookie(user));
        log.info("[Login Success] = {}", user);
        setRedirectHeaderToMain(response);
    }

    private void requestLoginPage(HttpRequest request, HttpResponse response) {
        response.setView(ViewResolver.getView("login.html"));
        response.setStatus(HttpStatus.OK);
        response.setHeaders(HttpHeaders.create(request, response));
    }
}
