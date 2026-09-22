package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isLoggedIn(request.getSessionId())) {
            response.redirect("/index.html");
            return;
        }

        response.sendStaticResource(request.getResourcePath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);

        if (account == null || password == null) {
            response.redirect("/401.html");
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> {
                            SessionManager.remove(request.getSessionId());
                            Session session = createSession();
                            session.setAttribute("user", user);
                            log.info("조회된 사용자: id={}, account={}", user.getId(), user.getAccount());
                            response.addCookie(Cookie.createJSessionId(session.getId()));
                            response.redirect("/index.html");
                        },
                        () -> response.redirect("/401.html")
                );
    }

    private boolean isLoggedIn(String sessionId) {
        Session session = SessionManager.findSession(sessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private Session createSession() {
        Session session = new Session(java.util.UUID.randomUUID().toString());
        SessionManager.add(session);

        return session;
    }
}
