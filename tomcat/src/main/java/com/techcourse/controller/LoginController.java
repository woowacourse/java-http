package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.Resource;
import org.apache.catalina.resource.ResourceReader;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Optional;

public class LoginController extends AbstractController {
    private static final String SESSION_USER_KEY = "user";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.sendRedirect("/index.html");
            return;
        }
        final Resource resource = ResourceReader.read("/login.html").orElseThrow();
        response.setBody(resource.contentType(), resource.content());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        if (isBlank(account) || isBlank(password)) {
            response.sendRedirect("/401.html");
            return;
        }

        final Optional<User> user = findUser(account, password);
        if (user.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        loginSuccess(user.get(), response);
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final String sessionId = request.getCookie().get(JSESSIONID);
        final Session session = SessionManager.INSTANCE.findSession(sessionId);
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private Optional<User> findUser(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void loginSuccess(final User user, final HttpResponse response) {
        final Session session = SessionManager.INSTANCE.createSession();
        session.setAttribute(SESSION_USER_KEY, user);

        response.sendRedirect("/index.html");
        response.setHeader("Set-Cookie", JSESSIONID + "=" + session.getId());
    }
}
