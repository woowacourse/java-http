package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.Session;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_SESSION_KEY = "user";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();
        validateBody(body);
        String account = body.get(ACCOUNT_KEY);
        String password = body.get(PASSWORD_KEY);

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            response.setLocation("401.html");
            response.setStatus(HttpStatusCode.FOUND);
            return;
        }

        login(request, response, user);
        response.setLocation("index.html");
        response.setStatus(HttpStatusCode.FOUND);
    }

    private void validateBody(Map<String, String> body) {
        if (!body.containsKey(ACCOUNT_KEY)) {
            throw new IllegalArgumentException("account가 존재하지 않습니다.");
        }

        if (!body.containsKey(PASSWORD_KEY)) {
            throw new IllegalArgumentException("password가 존재하지 않습니다.");
        }
    }

    private void login(HttpRequest request, HttpResponse response, User user) {
        Session session = Session.createRandomSession();
        if (request.existsSession()) {
            session = request.getSession();
        }
        session.setAttribute(USER_SESSION_KEY, user);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setLocation("login.html");
        response.setStatus(HttpStatusCode.FOUND);
    }
}
