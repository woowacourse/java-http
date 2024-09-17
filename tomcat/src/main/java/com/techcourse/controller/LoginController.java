package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class LoginController extends Controller {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        try {
            String sessionId = request.getCookieValue("JSESSIONID");
            User user = SessionManager.get(sessionId);
            log.info(user.toString());

            response.setStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeader.LOCATION, "/index.html");
        } catch (IllegalArgumentException e) {
            response.setBodyWithStaticResource("/login.html");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        try {
            String account = request.getBodyParameter("account");
            String password = request.getBodyParameter("password");

            String sessionId = login(account, password);

            response.setStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeader.SET_COOKIE, "JSESSIONID=" + sessionId);
            response.addHeader(HttpHeader.LOCATION, "/index.html");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBodyWithStaticResource("/401.html");
        }
    }

    private String login(String account, String password) {
        validateBlank(account, password);
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        validateUserExist(optionalUser);
        User user = optionalUser.get();
        validatePassword(user, password);

        String sessionId = SessionManager.put(user);
        log.info(user.toString());
        return sessionId;
    }

    private void validateBlank(String account, String password) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("parameter could not be blank.");
        }
    }

    private void validateUserExist(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("No such user.");
        }
    }

    private void validatePassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("invalid password.");
        }
    }
}
