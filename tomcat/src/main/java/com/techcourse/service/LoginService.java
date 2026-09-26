package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoginService {

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
}
