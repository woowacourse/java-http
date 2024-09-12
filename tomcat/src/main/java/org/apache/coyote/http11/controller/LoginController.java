package org.apache.coyote.http11.controller;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;
    private final UserService userService;

    public LoginController(UserService userService) {
        this.sessionManager = SessionManager.getInstance();
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isAlreadyLogin(request)) {
            response.setStatusLine("302", "Found");
            response.setFieldValue("Location", "/index.html");
            return;
        }

        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);

        User user = userService.authenticateUser(request.getUserInformation());
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
        String sessionId = request.getJSessionId();
        if (sessionId.isBlank()) {
            return false;
        }

        return Optional.ofNullable(sessionManager.findSession(sessionId))
                .map(session -> (User) session.getAttribute("user"))
                .isPresent();
    }

    private String createSession(User user) {
        String newSessionId = UUID.randomUUID().toString();
        Session session = new Session(newSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return newSessionId;
    }
}
