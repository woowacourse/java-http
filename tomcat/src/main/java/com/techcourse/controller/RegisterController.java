package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private static RegisterController instance;

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        if (instance == null) {
            instance = new RegisterController();
        }
        return instance;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setResourceName("/register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> values = BodyParser.parseValues(request.getBody());
        String account = values.get("account");
        String password = values.get("password");
        String email = values.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.found();
        response.redirectPage("/index.html");
    }
}
