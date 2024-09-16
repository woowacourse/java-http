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

public class RegisterController extends Controller {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        try {
            String sessionId = request.getCookieValue("JSESSIONID");
            User user = SessionManager.get(sessionId);

            log.info(user.toString());
            response.setStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeader.LOCATION, "/index.html");
        } catch (IllegalArgumentException e) {
            response.setBodyWithStaticResource("/register.html");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        try {
            String account = request.getBodyParameter("account");
            String password = request.getBodyParameter("password");
            String email = request.getBodyParameter("email");

            String sessionId = register(account, password, email);

            response.setStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeader.SET_COOKIE, "JSESSIONID=" + sessionId);
            response.addHeader(HttpHeader.LOCATION, "/index.html");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBodyWithStaticResource("/register.html");
        }
    }

    private String register(String account, String password, String email) {
        validateBlank(account, password, email);
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        validateUserExist(optionalUser);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info(user.toString());

        String sessionId = SessionManager.put(user);
        return sessionId;
    }

    private void validateBlank(String account, String password, String email) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("parameter could not be blank.");
        }
    }

    private void validateUserExist(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) {
            throw new IllegalArgumentException("user already exists.");
        }
    }
}
