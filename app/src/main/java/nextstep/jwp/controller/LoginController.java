package nextstep.jwp.controller;


import nextstep.jwp.application.LoginService;
import nextstep.jwp.model.http.HttpRequest;
import nextstep.jwp.model.http.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (loginService.isExistUser(request)) {
            response.redirect("/index.html");
            return;
        }

        response.redirect("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/login.html");
    }
}

