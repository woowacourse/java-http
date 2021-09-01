package nextstep.jwp.controller;

import nextstep.jwp.exception.BaseException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;

public class RegisterController extends AbstractController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.ok("/register.html");
    }


    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            userService.save(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"),
                    httpRequest.getQueryValue("email"));
            httpResponse.redirect("/index.html");
        } catch (BaseException e) {
            httpResponse.redirect("/401.html");
        }
    }
}
