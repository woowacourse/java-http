package org.apache.coyote.http11.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request_response.HttpRequest;
import org.apache.coyote.http11.request_response.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals("POST") && request.getUriPath().equals("/register");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        try {
            Map<String, String> formData = parseQueryParameters(request.getBody());
            register(formData);
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return HttpResponse.builder()
            .status(HttpStatus.Found)
            .header("Location", "/index.html")
            .body("")
            .build();
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(queryString.split("&"))
            .map(parameter -> parameter.split("="))
            .forEach(keyValue -> queryParameters.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null));
        return Collections.unmodifiableMap(queryParameters);
    }

    private void register(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        String email = queryParameters.get("email");
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("account and password and email should be not null");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("user register account {} and email {}", user, email);
    }
}
