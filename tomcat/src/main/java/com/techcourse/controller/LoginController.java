package com.techcourse.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.apache.catalina.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController extends AbstractController {
    private static final LoginController instance = new LoginController();
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String SESSION_ATTRIBUTE = "user";

    private final UserService userService = new UserService();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        try {
            RequestBody requestBody = request.getBody();
            String account = requestBody.getAttribute("account");
            String password = requestBody.getAttribute("password");

            User user = userService.login(account, password);
            log.info("User found: {}", user);

            Session session = getSession(request, user);
            if (Objects.isNull(session)) {
                session = createNewSession(user);
            }
            response.setCookie(HttpCookie.ofJSessionId(session.getId()));

            redirect("index.html", response);
        } catch (UnauthorizedException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            redirect("401.html", response);
        }
    }

    private Session getSession(HttpRequest request, User user) throws IOException {
        Session session = sessionManager.findSession(request)
                .orElse(sessionManager.getByAttribute(SESSION_ATTRIBUTE, user).orElse(null));
        if (Objects.nonNull(session)) {
            session = getOrInvalidateSession(user, session);
        }
        return session;
    }

    private static Session getOrInvalidateSession(User user, Session session) {
        if (isInvalidSession(user, session)) {
            sessionManager.remove(session);
            return null;
        }
        return session;
    }

    private static boolean isInvalidSession(User user, Session session) {
        return Objects.nonNull(session.getAttribute(SESSION_ATTRIBUTE)) && !Objects.equals(session.getAttribute(SESSION_ATTRIBUTE), user);
    }

    private Session createNewSession(User user) {
        Session newSession = Session.createRandomSession();
        newSession.setAttribute(SESSION_ATTRIBUTE, user);
        sessionManager.add(newSession);
        return newSession;
    }


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Optional<Session> session = sessionManager.findSession(request);
        if (session.isPresent() && Objects.nonNull(session.get().getAttribute(SESSION_ATTRIBUTE))) {
            redirect("index.html", response);
            return;
        }
        redirect("login.html", response);
    }

    private static void redirect(String location, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
    }
}
