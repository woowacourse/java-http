package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.REGISTER_PAGE;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String STATIC_PATH = "static";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        register(request, response);
    }

    private void register(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> requestParam = request.getRequestBody().getParam();
        final User registeredUser = new User(requestParam.get(ACCOUNT), requestParam.get(PASSWORD), requestParam.get(EMAIL));
        InMemoryUserRepository.save(registeredUser);
        response.foundResponse(INDEX_PAGE.gerResource());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String responseBody = ResourceReader.readResource(STATIC_PATH + REGISTER_PAGE.gerResource());
        response.getResponse(request, responseBody);
    }
}
