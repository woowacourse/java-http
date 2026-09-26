package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResource;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String LOGIN_USER = "user";

    private final SessionManager sessionManager;
    private final StaticResource staticResource;

    public LoginController(SessionManager sessionManager, StaticResource staticResource) {
        this.sessionManager = sessionManager;
        this.staticResource = staticResource;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.getParameters().isEmpty()) {
            login(request, response);
            return;
        }

        if (isLoggedIn(request.getCookie())) {
            response.redirect("/index.html");
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        response.setBody(staticResource.read("/login.html").orElse(new byte[0]));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        login(request, response);
    }

    private void login(HttpRequest request, HttpResponse response) {
        User user = findUser(request);
        if (user == null) {
            response.redirect("/401.html");
            return;
        }

        Session session = findSession(request.getCookie());
        if (session == null) {
            session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            response.addCookie(HttpCookie.JSESSION_ID, session.getId());
        }
        session.setAttribute(LOGIN_USER, user);
        response.redirect("/index.html");
    }

    private User findUser(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            return null;
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElse(null);
    }

    private boolean isLoggedIn(HttpCookie cookie) {
        Session session = findSession(cookie);
        return session != null && session.getAttribute(LOGIN_USER) != null;
    }

    private Session findSession(HttpCookie cookie) {
        return sessionManager.findSession(cookie.getJSessionId());
    }
}
