package org.apache.coyote.core.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.io.ClassPathResource;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        String method = request.getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        }
        if (method.equals("GET")) {
            doGet(request, response);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        registerUser(request);

        String responseBody = new ClassPathResource().getStaticContent(request.getPath());

        response.setStatus("FOUND");
        response.setContentType(request.findContentType());
        response.setContentLength(responseBody.getBytes().length);
        response.setLocation("./index.html");
        response.setResponseBody(responseBody);
    }

    private void registerUser(final HttpRequest request) {
        Map<String, String> requestBodies = request.getRequestBodies();
        String account = requestBodies.get("account");
        String password = requestBodies.get("password");
        String email = requestBodies.get("email");
        validateUserInformation(account, password, email);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void validateUserInformation(final String account, final String password, final String email) {
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        String responseBody = new ClassPathResource().getStaticContent(request.getPath());

        response.setStatus("OK");
        response.setContentType(request.findContentType());
        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
    }
}
