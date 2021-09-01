package nextstep.jwp.controller;

import nextstep.jwp.exception.BaseException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
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
