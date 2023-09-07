package org.apache.coyote.handler.controller.register;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.RequestController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.util.Map;

import static org.apache.coyote.handler.controller.Path.MAIN;

public class RegisterController extends RequestController {
    private static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isPostRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> bodyParams = request.getParsedBody();

        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");
        final String email = bodyParams.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.mapToRedirect(MAIN.getPath());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }
}
