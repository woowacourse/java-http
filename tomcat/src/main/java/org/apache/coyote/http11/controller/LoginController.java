package org.apache.coyote.http11.controller;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        super.doGet(request, response);

        if (isAlreadyLogin(request)) {
            response.setStatusLine("302", "Found");
            response.setFieldValue("Location", "/index.html");
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);

        User user = request.getUser();
        if (isLoginFail(user)) {
            response.setFieldValue("Location", "/401.html");
            return;
        }
        response.setFieldValue("Location", "/index.html");

        String jSessionId = createSession(user);
        response.setFieldValue("Set-Cookie", "JSESSIONID=" + jSessionId);
    }

    private boolean isLoginFail(User user) {
        return Objects.isNull(user);
    }

    private boolean isAlreadyLogin(HttpRequest request) {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = request.getSession();
        if (sessionId.isBlank()) {
            return false;
        }

        return Optional.ofNullable(sessionManager.findSession(sessionId))
                .map(session -> (User) session.getAttribute("user"))
                .isPresent();
    }

    private String createSession(User user) {
        final SessionManager sessionManager = SessionManager.getInstance();

        String newSessionId = UUID.randomUUID().toString();
        Session session = new Session(newSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return newSessionId;
    }
}
