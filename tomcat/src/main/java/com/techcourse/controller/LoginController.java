package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.util.StaticResources;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Session session = findSession(request);
        if (session != null) {
            log.info("이미 로그인된 사용자: {}", session.getAttribute(USER));
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        response.okHtml(StaticResources.read(LOGIN_PAGE).orElse(""));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Optional<User> user = login(request);
        if (user.isEmpty()) {
            log.info("로그인 실패: {}", request.getParameter(ACCOUNT));
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId);
        session.setAttribute(USER, user.get());
        SESSION_MANAGER.add(session);

        log.info("로그인 성공: {}", user.get());
        response.sendRedirect(INDEX_PAGE);
        response.addJSessionId(sessionId);
    }

    private Session findSession(final HttpRequest request) {
        final String sessionId = request.getCookie().getJSessionId();
        if (sessionId == null) {
            return null;
        }
        return SESSION_MANAGER.findSession(sessionId);
    }

    private Optional<User> login(final HttpRequest request) {
        final String account = request.getParameter(ACCOUNT);
        final String password = request.getParameter(PASSWORD);
        if (account == null || password == null) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
