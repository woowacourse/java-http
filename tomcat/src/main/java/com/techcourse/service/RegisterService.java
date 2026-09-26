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

public class RegisterService {

    public boolean register(String body) {
        Map<String, String> parameters = parseBody(body);
        String account = parameters.get("account");
        String password = parameters.get("password");
        String email = parameters.get("email");

        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            return false;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return true;
    }

    public String readRegisterPage() throws IOException, URISyntaxException {
        URL registerPage = ClassLoader.getSystemResource("static/register.html");
        if (registerPage == null) {
            return null;
        }
        Path path = Path.of(registerPage.toURI());
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
