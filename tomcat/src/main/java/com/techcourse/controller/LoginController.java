package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.catalina.Session;
import org.apache.coyote.http11.request.HttpRequest;
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
    public HttpResponse handle(HttpRequest request) throws IOException {
        try {
            HttpResponse response = operate(request);
            return response;
        } catch (UnauthorizedException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            return redirect("401.html");
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        Map<String, String> requestBody = request.getBody().parseRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        HttpResponse response = new HttpResponse();
        Session session = sessionManager.findSession(request).orElse(Session.createRandomSession());
        session.setAttribute(SESSION_ATTRIBUTE, user.getAccount());
        sessionManager.add(session);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));

        return redirect("index.html", response);
    }


    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        Optional<Session> session = sessionManager.findSession(request);
        if (session.isPresent() && Objects.nonNull(session.get().getAttribute(SESSION_ATTRIBUTE))) {
            return redirect("index.html", new HttpResponse());
        }
        return redirect("login.html");
    }

    private static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);

        return response;
    }

    private static HttpResponse redirect(String location, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
        return response;
    }
}
