package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.SessionManager;
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
            response.addHeader("Location", "/index.html");
        } catch (IllegalArgumentException e) {
            response.setBodyWithStaticResource("/login.html");
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");

        if (account == null || password == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBodyWithStaticResource("/401.html");
            return;
        }

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isEmpty() || !optionalUser.get().checkPassword(password)) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBodyWithStaticResource("/401.html");
            return;
        }

        User user = optionalUser.get();
        log.info(user.toString());
        String sessionId = SessionManager.put(user);

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        response.addHeader("Location", "/index.html");
    }
}
