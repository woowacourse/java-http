package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (isLoggedIn(request.getCookie("JSESSIONID"))) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        response.setBody(ContentType.HTML, StaticResourceLoader.load(LOGIN_PAGE));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        if (loginUser.isEmpty()) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", loginUser.get());
        SessionManager.add(session);
        log.info("user : {}", loginUser.get().getAccount());
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        response.sendRedirect(INDEX_PAGE);
    }

    private boolean isLoggedIn(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        Session session = SessionManager.findSession(sessionId);
        return session != null && session.getAttribute("user") != null;
    }
}
