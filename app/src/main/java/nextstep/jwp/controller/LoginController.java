package nextstep.jwp.controller;

import nextstep.jwp.exception.BaseException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

public class LoginController extends AbstractController{

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (isLogin(httpRequest.getSession())) {
            httpResponse.redirect("/index.html");
            return;
        }

        httpResponse.ok("/login.html");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            User loginUser = userService.login(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"));
            HttpSession session = httpRequest.getSession();
            session.setAttribute("user", loginUser);
            httpResponse.redirect("/index.html");
        } catch (BaseException e) {
            httpResponse.redirect("/401.html");
        }
    }
}
