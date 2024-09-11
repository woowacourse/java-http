package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LoginController() {
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setResourceName("/login.html");
        response.ok();
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> values = BodyParser.parseValues(request.getBody());
        String account = values.get("account");
        String password = values.get("password");
        if (account == null || password == null) {
            failLogin(response);
            return;
        }
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> successLogin(user, response),
                        () -> failLogin(response)
                );
        response.found();
    }

    private void successLogin(User user, HttpResponse response) {
        LOGGER.info(user.toString());
        response.redirectPage("/index.html");
    }

    private void failLogin(HttpResponse response) {
        response.redirectPage("/401.html");
    }
}
