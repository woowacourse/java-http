package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Path filePath = getFilePath("/register");
        response.set(HttpStatus.OK, filePath, Files.readString(filePath), null);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        createUser(request);
        response.set(HttpStatus.FOUND, getFilePath("/index.html"), "", "/index.html");
    }

    private void createUser(final HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("registered user: {}", user);
    }

    private Path getFilePath(final String uriPath) {
        final String resourceName = "static/" + (uriPath.startsWith("/") ? uriPath.substring(1) : uriPath);
        return resolveResourcePath(resourceName);
    }

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            return Path.of("/");
        }
        return Path.of(url.getPath());
    }
}
