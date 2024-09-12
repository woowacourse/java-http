package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    public ForwardResult execute(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();

        String account = body.get(ACCOUNT_KEY);
        String password = body.get(PASSWORD_KEY);

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            User user = optionalUser.get();
            return ForwardResult.ofRedirect("index.html");
        }

        return ForwardResult.ofRedirect("401.html");
    }
}
