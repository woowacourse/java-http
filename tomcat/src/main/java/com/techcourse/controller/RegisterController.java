package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();
        validateBody(body);
        String account = body.get(ACCOUNT_KEY);
        String email = body.get(EMAIL_KEY);
        String password = body.get(PASSWORD_KEY);

        InMemoryUserRepository.save(new User(account, password, email));

        response.setLocation("index.html");
        response.setStatus(HttpStatusCode.FOUND);
    }

    private void validateBody(Map<String, String> body) {
        if (!body.containsKey(ACCOUNT_KEY)) {
            throw new IllegalArgumentException("account가 존재하지 않습니다.");
        }

        if (!body.containsKey(EMAIL_KEY)) {
            throw new IllegalArgumentException("email가 존재하지 않습니다.");
        }

        if (!body.containsKey(PASSWORD_KEY)) {
            throw new IllegalArgumentException("password가 존재하지 않습니다.");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setLocation("register.html");
        response.setStatus(HttpStatusCode.FOUND);
    }
}
