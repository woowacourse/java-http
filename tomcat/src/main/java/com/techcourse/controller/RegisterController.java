package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PATH = "/register";

    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var resource = getClass().getClassLoader().getResource("static/register.html");
        response.setBody(Files.readAllBytes(Path.of(resource.toURI())));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (register(request.getParameters())) {
            response.sendRedirect(INDEX_PAGE);
        } else {
            response.sendRedirect(REGISTER_PATH);
        }
    }

    private boolean register(final Map<String, String> formParameters) {
        final var account = formParameters.get(ACCOUNT_PARAMETER);
        final var password = formParameters.get(PASSWORD_PARAMETER);
        final var email = formParameters.get(EMAIL_PARAMETER);
        if (account == null || password == null || email == null) {
            return false;
        }
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("register success: {}", user);
        return true;
    }
}
