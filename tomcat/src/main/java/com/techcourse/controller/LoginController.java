package com.techcourse.controller;

import com.techcourse.except.UnauthorizedException;
import com.techcourse.except.UserNotFoundException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private static final String URI = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String DEFAULT_PATH = "static";
    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    private final UserService userService;
    private final SessionManager sessionManager = SessionManager.getInstance();

    public LoginController(UserService userService) {
        super(URI);
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> params = request.getKeyValueBodies();
        String account = params.get(ACCOUNT);
        String password = params.get(PASSWORD);

        try {
            User user = userService.findBy(account);
            userService.checkUser(user, password);
            handleSuccessfulLogin(response, user);
        } catch (UserNotFoundException | UnauthorizedException e) {
            handleFailedLogin(response);
        }
    }

    private void handleSuccessfulLogin(HttpResponse response, User user) {
        HttpCookie httpCookie = makeUserCookie(user);

        response.setStatusLine(HttpStatus.FOUND);
        response.setHeader(HttpHeaderField.LOCATION.getName(), INDEX_HTML);
        response.setHeader(HttpHeaderField.SET_COOKIE.getName(), httpCookie.toString());
        LOGGER.info("로그인한 회원: {}", user);
    }

    private void handleFailedLogin(HttpResponse response) {
        String path = DEFAULT_PATH + UNAUTHORIZED_HTML;

        response.setStaticBody(path);
        response.setStatusLine(HttpStatus.UNAUTHORIZED);
    }

    private HttpCookie makeUserCookie(User user) {
        Session session = makeUserSession(user);

        HttpCookie httpCookie = new HttpCookie(Session.JSESSIONID, session.getId());
        httpCookie.setHttpOnly(true);
        return httpCookie;
    }

    private Session makeUserSession(User user) {
        Session session = new Session();
        session.setAttribute(USER, user);
        sessionManager.add(session);
        return session;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(HttpStatus.OK);

        HttpCookies cookies = request.getCookies();
        HttpCookie cookie = cookies.getCookie(Session.JSESSIONID);

        String path = determinePagePath(cookie);
        response.setStaticBody(path);
    }

    private String determinePagePath(HttpCookie sessionCookie) {
        if (sessionManager.isExistSession(sessionCookie.getValue())) {
            return DEFAULT_PATH + INDEX_HTML;
        }
        return DEFAULT_PATH + LOGIN_HTML;
    }
}
