package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.ForwardResult;
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
    public ForwardResult execute(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();
        String account = body.get(ACCOUNT_KEY);
        String password = body.get(PASSWORD_KEY);

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            return ForwardResult.ofRedirect("401.html");
        }

        login(request, response, user);
        return ForwardResult.ofRedirect("index.html");
    }

    private void login(HttpRequest request, HttpResponse response, User user) {
        Session session = Session.createRandomSession();
        if (request.existsSession()) {
            session = request.getSession();
        }
        session.setAttribute(USER_SESSION_KEY, user);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));
    }
}
