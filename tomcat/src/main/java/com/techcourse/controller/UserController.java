package com.techcourse.controller;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;

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
}
