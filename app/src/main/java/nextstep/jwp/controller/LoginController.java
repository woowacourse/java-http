package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.session.HttpSession;
import nextstep.jwp.session.HttpSessions;

import static nextstep.jwp.PageUrl.*;

public class LoginController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoginStatus(request.getSession())) {
            response.redirect(INDEX_PAGE.getPath());
            return;
        }
        response.forward(LOGIN_PAGE.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getRequestBodyParam("account");
        String password = request.getRequestBodyParam("password");

        User user = getUser(response, account);

        if (!user.checkPassword(password)) {
            response.redirect(UNAUTHORIZED_PAGE.getPath());
            return;
        }

        if (request.hasNoSessionId()) {
            String sessionId = String.valueOf(UUID.randomUUID());
            HttpSession session = HttpSessions.getSession(sessionId);
            session.setAttribute("user", user);
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
        }
        response.redirect(INDEX_PAGE.getPath());
    }

    private boolean isLoginStatus(HttpSession session) {
        Object user = session.getAttributes("user");
        return Objects.nonNull(user);
    }

    private User getUser(HttpResponse response, String account) throws IOException {
        User user = null;
        try {
            user = InMemoryUserRepository.findByAccount(account);
        } catch (IllegalArgumentException e) {
            response.redirect(UNAUTHORIZED_PAGE.getPath());
        }
        return user;
    }
}

