package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String ACCOUNT = "account";

    private static final String PASSWORD = "password";

    private static final String EMAIL = "email";

    private static final String INDEX_PAGE = "/index.html";

    private static final String REGISTER_PAGE = "/register.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.forward(REGISTER_PAGE);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {

        final Optional<String> account = getRequiredParameter(request, ACCOUNT);
        final Optional<String> password = getRequiredParameter(request, PASSWORD);
        final Optional<String> email = getRequiredParameter(request, EMAIL);

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            response.forward(REGISTER_PAGE);
            return;
        }

        final User user = new User(account.get(), password.get(), email.get());
        InMemoryUserRepository.save(user);

        log.info("register success account: {}", user.getAccount());

        response.sendRedirect(INDEX_PAGE);
    }

    private Optional<String> getRequiredParameter(final HttpRequest request, final String name) {
        return request.getParameter(name)
                .filter(value -> !value.isBlank());
    }
}