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

public class LoginController extends Controller {
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
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        try {
            operate(request, response);
        } catch (UnauthorizedException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            redirect("401.html", response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getAttribute("account");
        String password = requestBody.getAttribute("password");

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        Session session = getSession(request, user);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));

        redirect("index.html", response);
    }

    private Session getSession(HttpRequest request, User user) throws IOException {
        Session session = sessionManager.findSession(request).orElse(Session.createRandomSession());
        session.setAttribute(SESSION_ATTRIBUTE, user.getAccount());
        sessionManager.add(session);
        return session;
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
