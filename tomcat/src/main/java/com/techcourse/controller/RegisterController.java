package com.techcourse.controller;

import com.techcourse.model.RegisterService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(final RegisterService registerService) {
        Objects.requireNonNull(registerService, "registerService must not be null");
        this.registerService = registerService;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final var bodyBytes = readStaticFile("static/register.html");

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
        final var email = request.getParameter("email");

        registerService.register(account, password, email);

        return HttpResponse.builder()
                .status(302, "Found")
                .header("Location", "/index.html")
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
