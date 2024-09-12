package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.message.common.HttpHeaderField;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService = UserService.getInstance();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        Map<String, String> params = request.getKeyValueBodies();
        String account = params.get("account");
        String password = params.get("password");

        if (!userService.isAccountExist(account)) {
            handleFailedLogin(response);
            return;
        }

        User user = userService.findUserByAccount(account);
        if (!userService.isPasswordCorrect(user, password)) {
            handleFailedLogin(response);
            return;
        }

        handleSuccessfulLogin(response, user);
    }

    private void handleFailedLogin(HttpResponse response) throws IOException, URISyntaxException {
        String path = "static/401.html";

        response.setStaticBody(path);
        response.setStatusLine(HttpStatus.UNAUTHORIZED);
    }

    private void handleSuccessfulLogin(HttpResponse response, User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);

        HttpCookie httpCookie = new HttpCookie(Session.JSESSIONID);
        httpCookie.setValue(session.getId());
        httpCookie.setHttpOnly(true);

        response.setStatusLine(HttpStatus.FOUND);
        response.setHeader(HttpHeaderField.LOCATION.getName(), "/index.html");
        response.setHeader(HttpHeaderField.SET_COOKIE.getName(), httpCookie.toString());
        log.info("User logged in: {}", user);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(HttpStatus.OK);

        HttpCookies cookies = request.getCookies();
        HttpCookie cookie = cookies.getCookie(Session.JSESSIONID);

        String path = determinePagePath(cookie);
        response.setStaticBody(path);
    }

    private String determinePagePath(HttpCookie sessionCookie) {
        if (sessionManager.isExistSession(sessionCookie.getValue())) {
            return "static/index.html";
        }
        return "static/login.html";
    }
}
