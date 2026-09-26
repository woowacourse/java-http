package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import org.apache.catalina.Session;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException, URISyntaxException {
        if (isLoggedIn(request)) {
            return HttpResponse.redirect("/index.html");
        }

        String body = loginService.readLoginPage();
        if (body == null) {
            return HttpResponse.create("404 Not Found", "text/plain", "Not Found");
        }
        return HttpResponse.create("200 OK", "text/html", body);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getFormParameter("account");
        String password = request.getFormParameter("password");
        User user = loginService.authenticate(account, password);
        if (user == null) {
            return HttpResponse.redirect("/401.html");
        }

        Session session = request.getSession(true);
        session.setAttribute("user", user);

        String requestedSessionId = request.getCookie().getValue("JSESSIONID");
        if (session.getId().equals(requestedSessionId)) {
            return HttpResponse.redirect("/index.html");
        }
        return HttpResponse.redirectWithCookie("/index.html", "JSESSIONID=" + session.getId());
    }

    private boolean isLoggedIn(HttpRequest request) {
        Session session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
}
