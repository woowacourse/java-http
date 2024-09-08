package com.techcourse.controller;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;

public class UserController {

    private static final URI UNAUTHORIZED_URI = URI.create("/401.html");

    public HttpResponseEntity<User> searchUserData(Map<String, String> params) {
        String account = params.getOrDefault("account", "");
        String password = params.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, null);
            httpResponse.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
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
            httpResponse.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
            return httpResponse;
        }

        HttpResponseEntity<User> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, user.get());
        httpResponse.addHeader(HttpHeaders.LOCATION, "/index.html");
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, new HttpCookie().makeSessionCookie().getCookieToMessage());
        return httpResponse;
    }

    public HttpResponseEntity<Void> registerUser(Map<String, String> data) {
        String account = data.getOrDefault("account", "");
        String email = data.getOrDefault("email", "");
        String password = data.getOrDefault("password", "");

        HttpResponseEntity<Void> httpResponse = new HttpResponseEntity<>(HttpStatus.FOUND, null);

        if (account.isEmpty() || email.isEmpty() || password.isEmpty()) {
            httpResponse.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
            return httpResponse;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            httpResponse.addHeader(HttpHeaders.LOCATION, UNAUTHORIZED_URI.getPath());
            return httpResponse;
        }

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        httpResponse.addHeader(HttpHeaders.LOCATION, "/index.html");
        return httpResponse;
    }
}
