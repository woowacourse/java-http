package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import org.apache.catalina.session.Cookies;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class LoginController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (alreadyLogin(request)) {
            redirectMain(request, response);
            return;
        }
        requestLoginPage(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (alreadyLogin(request) || request.hasNoBody()) {
            redirectMain(request, response);
            return;
        }
        requestLogin(request, response);
    }

    private static boolean alreadyLogin(HttpRequest request) {
        Session session = request.getSession(false);
        if (session == null || !session.hasValue("user")) {
            return false;
        }
        return true;
    }

    private static void redirectMain(HttpRequest request, HttpResponse response) {
        response.setHeaders(HttpHeaders.create(request, response));
        setRedirectHeaderToMain(response);
    }

    private static void setRedirectHeaderToMain(HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }

    private void requestLoginPage(HttpRequest request, HttpResponse response) {
        response.setView(ViewResolver.getView("login.html"));
        response.setStatus(HttpStatus.OK);
        response.setHeaders(HttpHeaders.create(request, response));
    }

    private static void requestLogin(HttpRequest request, HttpResponse response) {
        User user = LoginService.login(request.findFromBody("account"),
                request.findFromBody("password"));
        Session session = request.getSession(true);
        session.setAttribute("user", user);
        response.setHeaders(HttpHeaders.create(request, response));
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        setRedirectHeaderToMain(response);
    }
}
