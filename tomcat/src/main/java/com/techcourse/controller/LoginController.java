package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.request.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.login.LoginParser;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public final class LoginController extends AbstractController {
    private static final String SESSION_USER_KEY = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourceType = response.resourceType();
        if (findLoginUser(request).isEmpty()) {
            response.setResponse(HttpResponse.ok(StaticResourceLoader.load("/login.html"), resourceType));
            return;
        }
        response.setResponse(HttpResponse.redirect("/index.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> body = parseRequestBody(request);
        Optional<User> user = authenticateUser(body.get("account"), body.get("password"));
        if (user.isEmpty()) {
            response.setResponse(HttpResponse.redirect("/401.html"));
            return;
        }

        invalidateExistingSession(request);
        Session session = createSession(user.get());
        response.setResponse(HttpResponse.redirectWithCookie("/index.html", session.getId()));
    }

    private Map<String, String> parseRequestBody(HttpRequest request) {
        boolean isFormUrlEncoded = request.header("Content-Type")
            .map(value -> value.startsWith("application/x-www-form-urlencoded"))
            .orElse(false);
        return isFormUrlEncoded ? LoginParser.parseQueryString(request.body()) : Map.of();
    }

    private Optional<User> authenticateUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password));
    }

    private Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(SESSION_USER_KEY, user);
        SessionManager.add(session);
        return session;
    }

    private void invalidateExistingSession(HttpRequest request) {
        Cookie cookies = Cookie.from(request.header("Cookie").orElse(""));
        Session existingSession = SessionManager.findSession(cookies.get("JSESSIONID"));
        if (existingSession != null) {
            existingSession.invalidate();
        }
    }

    private Optional<User> findLoginUser(HttpRequest request) {
        Cookie cookies = Cookie.from(request.header("Cookie").orElse(""));
        Session session = SessionManager.findSession(cookies.get("JSESSIONID"));
        if (session == null) {
            return Optional.empty();
        }
        Object user = session.getAttribute(SESSION_USER_KEY);
        return user instanceof User loginUser ? Optional.of(loginUser) : Optional.empty();
    }
}
