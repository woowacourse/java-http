package com.techcourse.controller;

import com.techcourse.model.LoginService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(final LoginService loginService) {
        Objects.requireNonNull(loginService, "loginService must not be null");
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final var session = request.getSession(true);

        if (session.getAttribute("user") != null) {
            return HttpResponse.builder()
                    .protocol(request.getProtocol())
                    .status(302, "Found")
                    .header("Location", "/index.html")
                    .contentType("text/html;charset=utf-8")
                    .build();
        }

        final var bodyBytes = readStaticFile("static/login.html");

        return HttpResponse.builder()
                .status(200, "OK")
                .contentType("text/html;charset=utf-8")
                .body(bodyBytes)
                .build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        final var session = request.getSession(true);

        if (loginService.login(account, password, session)) {
            return HttpResponse.builder()
                    .protocol(request.getProtocol())
                    .status(302, "Found")
                    .header("Location", "/index.html")
                    .contentType("text/html;charset=utf-8")
                    .build();
        }

        return HttpResponse.builder()
                .protocol(request.getProtocol())
                .status(302, "Found")
                .header("Location", "/401.html")
                .contentType("text/html;charset=utf-8")
                .build();
    }

    private byte[] readStaticFile(final String classpathLocation) throws Exception {
        final var resourceUrl = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource(classpathLocation),
                "Resource not found: " + classpathLocation
        );
        final var resourceUri = resourceUrl.toURI();

        return Files.readAllBytes(Paths.get(resourceUri));
    }
}
