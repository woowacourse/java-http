package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.catalina.session.SessionManager;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        response.addFileBody("/login.html");
        if (isLoginActive(request)) {
            response.sendRedirection("/index.html");
        }
    }

    private boolean isLoginActive(Request request) {
        return request.getLoginCookie()
                .map(cookie -> sessionManager.contains(cookie.getValue()))
                .orElse(false);
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        Map<String, String> userInfo = request.parseBody();
        String account = userInfo.get("account");
        String password = userInfo.get("password");

        Optional<User> rawUser = InMemoryUserRepository.findByAccount(account);
        if (rawUser.isEmpty() || !rawUser.get().checkPassword(password)) {
            failLogin(response);
            return;
        }
        successLogin(response, rawUser.get());
    }

    private void failLogin(Response response) throws URISyntaxException, IOException {
        response.unauthorized();
        response.addFileBody("/401.html");
    }

    private void successLogin(Response response, User user) {
        log.info("로그인 사용자 정보: {}", user);
        String sessionId = sessionManager.create("user", user);
        response.addLoginCookie(sessionId);
        response.sendRedirection("/index.html");
    }
}
