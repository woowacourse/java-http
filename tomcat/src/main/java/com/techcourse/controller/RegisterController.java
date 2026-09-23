package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {

        final String account = request.getParameter(ACCOUNT).orElse(null);
        final String password = request.getParameter(PASSWORD).orElse(null);
        final String email = request.getParameter(EMAIL).orElse(null);

        if (account == null || password == null || email == null) {
            return;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("register success account: {}", account);

        response.sendRedirect(INDEX_PAGE);
    }
}