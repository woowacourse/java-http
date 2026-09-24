package com.techcourse.api.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private final Manager sessionManager;

    public LoginController() {
        this(SessionManager.getInstance());
    }

    public LoginController(final Manager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        final Session session = findSession(request);
        if (session != null && session.getAttribute("user") != null) {
            response.setStatus(HttpStatus.FOUND);
            response.setContentType(ContentType.HTML);
            response.setBody(HttpResponse.resolveResource(INDEX_PAGE));
            response.setHeader("Location", INDEX_PAGE);
            return;
        }

        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.HTML);
        response.setBody(HttpResponse.resolveResource(LOGIN_PAGE));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        final Map<String, String> parameters = parseRequestBody(request);
        final Optional<User> loginUser = InMemoryUserRepository.findByAccount(parameters.get("account"))
                .filter(user -> user.checkPassword(parameters.get("password")));

        if (loginUser.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setContentType(ContentType.HTML);
            response.setBody(HttpResponse.resolveResource(UNAUTHORIZED_PAGE));
            return;
        }

        final Session session = getOrCreateSession(request);
        final User user = loginUser.get();
        session.setAttribute("user", user);
        log.info("로그인 성공: {}", user);

        response.setStatus(HttpStatus.FOUND);
        response.setContentType(ContentType.HTML);
        response.setBody(HttpResponse.resolveResource(INDEX_PAGE));
        response.setHeader("Location", INDEX_PAGE);
        if (request.getSessionId() == null) {
            response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        }
    }

    private Session findSession(final HttpRequest request) throws IOException {
        final String sessionId = request.getSessionId();
        if (sessionId == null) {
            return null;
        }
        return sessionManager.findSession(sessionId);
    }

    private Session getOrCreateSession(final HttpRequest request) throws IOException {
        final Session session = findSession(request);
        if (session != null) {
            return session;
        }

        final String sessionId = request.getSessionId() == null
                ? UUID.randomUUID().toString()
                : request.getSessionId();
        final Session newSession = new Session(sessionId);
        sessionManager.add(newSession);
        return newSession;
    }

}
