package com.techcourse.controller;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;

public class UserController {

    public HttpResponseEntity<User> searchUserData(Map<String, String> params) {
        String account = params.getOrDefault("account", "");
        String password = params.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            httpResponse.addHeader(HttpHeaders.LOCATION, "/401.html");
            return httpResponse;
        }

        HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, user.get());
        httpResponse.addHeader(HttpHeaders.LOCATION, "/index.html");
        return httpResponse;
    }

    public HttpResponseEntity<User> login(Map<String, String> params) {
        String account = params.getOrDefault("account", "");
        String password = params.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            httpResponse.addHeader(HttpHeaders.LOCATION, "/401.html");
            return httpResponse;
        }

        HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, user.get());
        httpResponse.addHeader(HttpHeaders.LOCATION, "/index.html");
        return httpResponse;
    }

    public HttpResponseEntity<Void> registerUser(Map<String, String> data) {
        String account = data.getOrDefault("account", "");
        String email = data.getOrDefault("email", "");
        String password = data.getOrDefault("password", "");

        HttpResponseEntity<Void> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, null);

        if (account.isEmpty() || email.isEmpty() || password.isEmpty()) {
            httpResponse.addHeader(HttpHeaders.LOCATION, "/401.html");
            return httpResponse;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            httpResponse.addHeader(HttpHeaders.LOCATION, "/401.html");
            return httpResponse;
        }

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        httpResponse.addHeader(HttpHeaders.LOCATION, "/index.html");
        return httpResponse;
    }
}
