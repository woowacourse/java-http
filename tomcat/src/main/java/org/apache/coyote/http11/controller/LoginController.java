package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager =
            SessionManager.getInstance();

    @Override
    protected void doGet(
            HttpRequest request,
            HttpResponse response
    ) throws IOException {
        Session session =
                sessionManager.findSession(request);

        if (session != null
                && session.getAttribute("user") != null) {
            response.addHeader("Location", "/index.html");
            return;
        }

        response.fromResource("login.html");
    }

    @Override
    protected void doPost(
            HttpRequest request,
            HttpResponse response
    ) throws IOException {
        Optional<User> user = authenticate(request);

        if (user.isEmpty()) {
            response.fromResource("401.html");
            return;
        }

        Session session =
                sessionManager.findSession(request);

        if (session == null) {
            session = sessionManager.createSession(response);
        }

        session.setAttribute("user", user.get());
        response.addHeader("Location", "/index.html");
    }

    private Optional<User> authenticate(HttpRequest request) {
        String account =
                request.getBodyValue("account");

        String password =
                request.getBodyValue("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository
                .findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
