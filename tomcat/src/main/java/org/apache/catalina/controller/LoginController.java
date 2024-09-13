package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Cookie;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController extends MappingController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String INDEX_PATH = "/index.html";
    private static final String LOGIN_PATH = "/login.html";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String sessionId = request.getSessionId();
        Session session = sessionManager.findSession(sessionId);
        if (session.isPresent() && session.getAttribute("user") != null) {
            response.setStatusLine(Status.FOUND);
            response.sendRedirect(INDEX_PATH);
            return;
        }
        response.forward(LOGIN_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> params = request.getBody();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            login(response, user, params);
            return;
        }
        response.setStatusLine(Status.UNAUTHORIZED);
        response.sendRedirect(UNAUTHORIZED_PATH);
    }

    private void login(HttpResponse response, User user, Map<String, String> params) {
        if (user.checkPassword(params.get("password"))) {
            Session session = saveSession(user);
            log.info("{}", user);
            response.setStatusLine(Status.FOUND);
            response.sendRedirect(INDEX_PATH);
            response.setCookie(Cookie.ofSessionId(session.getId()));
            return;
        }
        response.setStatusLine(Status.UNAUTHORIZED);
        response.sendRedirect(UNAUTHORIZED_PATH);
    }

    private Session saveSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
