package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String REGISTER_PAGE = "/register";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private final ResourceRenderer renderer = new ResourceRenderer();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        renderer.render(REGISTER_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        register(request);
        response.sendRedirect(INDEX_PAGE);
    }

    private void register(final HttpRequest request) {
        final Map<String, String> params = request.getBodyParams();
        final User user = new User(
                params.get(ACCOUNT),
                params.get(PASSWORD),
                params.get(EMAIL));
        InMemoryUserRepository.save(user);
        log.info("회원가입: {}", user);
    }
}
