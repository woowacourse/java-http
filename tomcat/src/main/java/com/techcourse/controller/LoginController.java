package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_USER_KEY = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request.getSession(false))) {
            redirect(response, "/index.html");
            return;
        }

        handleLogin(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        handleLogin(request, response);
    }

    private void handleLogin(final HttpRequest request, final HttpResponse response) throws IOException {
        if (!request.hasParameters()) {
            final Path filePath = getFilePath("/login");
            response.set(HttpStatus.OK, filePath, Files.readString(filePath), null);
            return;
        }

        final Optional<User> user = authenticate(request.getParameter("account"), request.getParameter("password"));
        if (user.isPresent()) {
            request.getSession().setAttribute(LOGIN_USER_KEY, user.get());
            redirect(response, "/index.html");
            return;
        }

        redirect(response, "/401.html");
    }

    private void redirect(final HttpResponse response, final String location) {
        response.set(HttpStatus.FOUND, getFilePath(location), "", location);
    }

    private boolean isLoggedIn(final Session session) {
        return session != null && session.getAttribute(LOGIN_USER_KEY) != null;
    }

    private Optional<User> authenticate(final String account, final String password) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            log.error("login error");
            return Optional.empty();
        }

        log.info("user : {}", user.get());
        return user;
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
