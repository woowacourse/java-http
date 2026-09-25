package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.controller.MethodDispatchingController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class RegisterController extends MethodDispatchingController {
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";

    private final StaticResourceController staticResourceController;

    public RegisterController(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        staticResourceController.serve("/register.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> formParameters = request.formParameters();
        final String account = getRequiredParameter(formParameters, ACCOUNT_PARAMETER);
        final String password = getRequiredParameter(formParameters, PASSWORD_PARAMETER);
        final String email = getRequiredParameter(formParameters, EMAIL_PARAMETER);

        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }

    private String getRequiredParameter(final Map<String, String> formParameters, final String name) {
        final String value = formParameters.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing form parameter: " + name);
        }
        return value;
    }
}
