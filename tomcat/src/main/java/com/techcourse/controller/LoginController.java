package com.techcourse.controller;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String INDEX_HTML_URL = "http://localhost:8080/index.html";
    private static final String UNAUTHORIZED_HTML_URL = "http://localhost:8080/401.html";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (!request.hasSession()) {
            response.setBodyFromStaticResource("/login.html");
            return;
        }

        response.setFound(INDEX_HTML_URL);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        validateFormParameters(request);
        try {
            User user = findUser(request);
            log.info("로그인 성공! account: {}", user.getAccount());

            response.setFound(INDEX_HTML_URL);
            Session session = request.getSession();
            session.setAttribute("user", user);
            response.setSessionCookie(session);
        } catch (UnauthorizedException e) {
            response.setFound(UNAUTHORIZED_HTML_URL);
        }
    }

    private void validateFormParameters(HttpRequest request) {
        if (!request.hasFormParameters()) {
            throw new IllegalArgumentException("로그인에 필요한 데이터가 오지 않았습니다.");
        }
    }

    private User findUser(HttpRequest request) {
        String account = request.getFormParameter("account");
        String password = request.getFormParameter("password");

        return userService.findUserByAccountAndPassword(account, password);
    }
}
