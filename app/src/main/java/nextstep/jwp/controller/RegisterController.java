package nextstep.jwp.controller;

import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

import static nextstep.jwp.application.UserService.*;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        response.forward("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        User saveUser = userService.save(request);
        response.redirect("/index.html");
    }
}
