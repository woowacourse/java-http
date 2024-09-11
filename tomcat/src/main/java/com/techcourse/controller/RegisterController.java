package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    @Override
    public ForwardResult execute(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();

        String account = body.get(ACCOUNT_KEY);
        String email = body.get(EMAIL_KEY);
        String password = body.get(PASSWORD_KEY);

        InMemoryUserRepository.save(new User(account, password, email));

        return new ForwardResult(HttpStatusCode.FOUND, "index.html");
    }
}
