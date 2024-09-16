package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.SessionManager;
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
        String bodyInputAccount = request.getBodyParameter("account");
        String bodyInputPassword = request.getBodyParameter("password");
        String bodyInputEmail = request.getBodyParameter("email");

        if (bodyInputAccount == null || bodyInputPassword == null || bodyInputEmail == null) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBodyWithStaticResource("/register.html");
            return;
        }

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(bodyInputAccount);

        if (!optionalUser.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBodyWithStaticResource("/register.html");
            return;
        }

        User user = new User(bodyInputAccount, bodyInputPassword, bodyInputEmail);
        InMemoryUserRepository.save(user);
        log.info(user.toString());

        String sessionId = SessionManager.put(user);
        response.setStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.SET_COOKIE, "JSESSIONID=" + sessionId);
        response.addHeader(HttpHeader.LOCATION, "/index.html");
    }
}
