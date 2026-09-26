package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LoginService {

    public User authenticate(String body) {
        Map<String, String> parameters = parseBody(body);
        return authenticate(parameters.get("account"), parameters.get("password"));
    }

    public User authenticate(String account, String password) {
        if (account == null || password == null) {
            return null;
        }

        User user = InMemoryUserRepository.findByAccount(account).orElse(null);
        if (user == null || !user.checkPassword(password)) {
            return null;
        }
        return user;
    }

    public String readLoginPage() throws IOException, URISyntaxException {
        URL loginPage = ClassLoader.getSystemResource("static/login.html");
        if (loginPage == null) {
            return null;
        }
        Path path = Path.of(loginPage.toURI());
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private Map<String, String> parseBody(String body) {
        Map<String, String> parameters = new HashMap<>();
        if (body.isBlank()) {
            return parameters;
        }

        for (String pair : body.split("&")) {
            String[] nameAndValue = pair.split("=", 2);
            String value = nameAndValue.length == 2 ? nameAndValue[1] : "";
            String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8);
            parameters.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return parameters;
    }
}
