package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.UrlEncodedParameters;

import java.util.Objects;

public final class RegisterController extends AbstractController {

    private static final String INDEX_PATH = "/index.html";

    private final Controller staticResourceController;

    public RegisterController() {
        this(new StaticResourceController());
    }

    public RegisterController(final Controller staticResourceController) {
        this.staticResourceController = Objects.requireNonNull(staticResourceController);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return staticResourceController.service(request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        return UrlEncodedParameters.parse(request.body())
                .map(this::register)
                .orElseGet(() -> HttpResponse.error(HttpStatus.BAD_REQUEST));
    }

    private HttpResponse register(final UrlEncodedParameters parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return HttpResponse.error(HttpStatus.BAD_REQUEST);
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        return HttpResponse.redirect(INDEX_PATH);
    }
}
