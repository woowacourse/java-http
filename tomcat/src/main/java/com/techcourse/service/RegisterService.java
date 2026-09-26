package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegisterService {

    public boolean register(String account, String password, String email) {
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
}
