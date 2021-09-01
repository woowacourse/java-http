package nextstep.jwp.controller;

import java.util.Objects;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController() {
        loginService = new LoginService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (loginService.isLoginUser(request, response)) {
            return;
        }

        response.forward("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody body = request.getRequestBody();

        User user = loginService.findUser(body);

        if (user.checkPassword(body.getParam("password"))) {
            log.debug("{} 님이 로그인 하였습니다.", user.getAccount());
            final HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);
            response.redirect("/index.html");
            return;
        }
        log.debug("아이디나 비밀번호가 틀립니다.");
        response.exception("/401.html");

    }
}
