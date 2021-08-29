package nextstep.jwp.controller;

import nextstep.jwp.application.UserService;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;

import java.io.IOException;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        userService.saveUser(request);
        response.redirect("/index.html");
    }
}
