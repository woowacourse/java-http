package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_ATTRIBUTE = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Cookie cookie = request.getCookie();
        if (isLoggedIn(cookie)) {
            log.info("이미 로그인된 사용자입니다. index.html로 이동합니다.");
            response.sendRedirect(INDEX_PAGE);
            return;
        }

        if (!cookie.hasJSessionId()) {
            response.addCookie(JSESSIONID, createSession().getId());
        }
        response.sendStaticResource("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");

        if (account == null || password == null) {
            log.info("아이디 또는 비밀번호가 입력되지 않았습니다.");
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("아이디 또는 비밀번호가 일치하지 않습니다. account: {}", account);
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        log.info("로그인 성공! 아이디 : {}", account);

        final Optional<Session> existingSession = findSession(request.getCookie());
        final Session session = existingSession.orElseGet(this::createSession);
        session.setAttribute(USER_ATTRIBUTE, user.get());

        if (existingSession.isEmpty()) {
            response.addCookie(JSESSIONID, session.getId());
        }
        response.sendRedirect(INDEX_PAGE);
    }

    private boolean isLoggedIn(final Cookie cookie) {
        return findSession(cookie)
                .map(session -> session.getAttribute(USER_ATTRIBUTE) != null)
                .orElse(false);
    }

    private Optional<Session> findSession(final Cookie cookie) {
        if (!cookie.hasJSessionId()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SessionManager.getInstance().findSession(cookie.getJSessionId()));
    }

    private Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SessionManager.getInstance().add(session);
        return session;
    }
}
