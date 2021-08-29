package nextstep.jwp.controller;


import nextstep.jwp.application.UserService;
import nextstep.jwp.model.httpMessage.request.HttpRequest;
import nextstep.jwp.model.httpMessage.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (userService.isExistUser(request)) {
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

