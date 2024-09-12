package org.apache.coyote.http11.controller;

import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);

        response.setFieldValue("Location", "/index.html");

        saveUser(request);
    }

    private void saveUser(HttpRequest request) {
        Map<String, String> userInformation = request.getUserInformation();

        String account = userInformation.getOrDefault("account", "");
        String password = userInformation.getOrDefault("password", "");
        String email = userInformation.getOrDefault("email", "");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
