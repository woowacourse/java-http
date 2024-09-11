package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import org.apache.ResourceReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestController extends AbstractRequestController {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestController.class);
    private static final String LOGIN_PAGE = "/login.html";
    private static final String SUCCESS_LOGIN_REDIRECTION_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_SESSION_ATTRIBUTE_NAME = "user";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (httpRequest.existsSession()) {
            Session session = httpRequest.getSession();
            User user = (User) session.getAttribute(USER_SESSION_ATTRIBUTE_NAME);
            log.info("세션 로그인 : " + user);
            httpResponse.found(SUCCESS_LOGIN_REDIRECTION_PATH);
            return;
        }
        String body = ResourceReader.readFile(LOGIN_PAGE);
        httpResponse.ok(MimeType.HTML, body, StandardCharsets.UTF_8);
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> param = httpRequest.getParsedBody();
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(param.get(ACCOUNT_KEY));
        if (isUserAuthorized(userOptional, param.get(PASSWORD_KEY))) {
            User user = userOptional.get();
            log.info("로그인 성공 : " + user);
            httpResponse.setSession(getSession(httpRequest, user));
            httpResponse.found(SUCCESS_LOGIN_REDIRECTION_PATH);
            return;
        }
        httpResponse.found(UNAUTHORIZED_PATH);
    }

    private boolean isUserAuthorized(Optional<User> userOptional, String password) {
        return userOptional
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    private Session getSession(HttpRequest httpRequest, User user) {
        Session session = httpRequest.getSession();
        session.setAttribute(USER_SESSION_ATTRIBUTE_NAME, user);
        SessionManager.getInstance().add(session);
        return session;
    }
}
