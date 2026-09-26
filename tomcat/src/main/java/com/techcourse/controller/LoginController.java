package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController implements Controller {

    private final Controller staticResourceController = new StaticResourceController();

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if ("GET".equals(request.requestLine().method())) {
            return showLoginPage(request);
        }

        Map<String, String> formData = request.body().parseFormData();
        String account = formData.get("account");
        String password = formData.get("password");

        Optional<User> authenticatedUser = authenticate(account, password);

        if (authenticatedUser.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }

        Session session = createSession(authenticatedUser);

        return HttpResponse.redirect("/index.html", new Cookie("JSESSIONID", session.getId()));
    }

    private HttpResponse showLoginPage(HttpRequest request) throws IOException {
        Optional<Cookie> sessionCookie = request.headers().getCookie("JSESSIONID");
        Session session = findSession(sessionCookie);

        if (isLoggedIn(session)) {
            return HttpResponse.redirect("/index.html");
        }

        return staticResourceController.handle(request);
    }

    private Session findSession(Optional<Cookie> sessionCookie) {
        if (sessionCookie.isEmpty()) {
            return null;
        }

        return SessionManager.findSession(sessionCookie.get().value());
    }

    private boolean isLoggedIn(Session session) {
        return session != null && session.getAttribute("user") != null;
    }

    @Nonnull
    private static Session createSession(Optional<User> authenticatedUser) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", authenticatedUser.get());
        SessionManager.add(session);

        return session;
    }

    private Optional<User> authenticate(String account, String password) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }

        if (password == null || password.isBlank()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
