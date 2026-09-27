package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterAbstractController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterAbstractController.class);

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");


    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setViewName("/register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        final String account = request.getBody().get("account");
        final String email = request.getBody().get("email");
        final String password = request.getBody().get("password");

        if (!isValidateData(account, email, password)) {
            response.badRequest();
            return;
        }

        try {
            final User user = new User(account, password, email);

            InMemoryUserRepository.save(user);
            request.getSession().setAttribute("user", user);

            response.setViewName("redirect:/index.html");
        } catch (Exception e) {
            e.printStackTrace();
            response.setViewName("redirect:/500.html");
        }
    }

    private boolean isValidateData(
            String account,
            String email,
            String password) {
        return isAccountValid(account) && isEmailValid(email) && isPasswordValid(password);
    }

    private boolean isAccountValid(String account) {
        return !Objects.isNull(account) && account.length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return !Objects.isNull(password) && password.length() >= 6;
    }

    private boolean isEmailValid(String email) {
        return !Objects.isNull(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getRequestLine().getPath().equals("/register");
    }
}
