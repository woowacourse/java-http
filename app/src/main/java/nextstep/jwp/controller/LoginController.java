package nextstep.jwp.controller;

import nextstep.jwp.exception.BaseException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.service.UserService;

public class LoginController extends AbstractController{

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.ok("/login.html");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            userService.login(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"));
            httpResponse.redirect("/index.html");
        } catch (BaseException e) {
            httpResponse.redirect("/401.html");
        }
    }
}
