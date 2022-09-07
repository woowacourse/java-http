package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;

public class RegisterHandler extends Handler {

    static final RegisterHandler REGISTER_HANDLER = new RegisterHandler();

    private static String SUCCESS_REDIRECT_URI = "/index.html";

    private RegisterHandler() {
    }

    @Override
    public HandlerResult handle(HttpRequest request) throws IOException {
        final String account = request.getBodyValue("account");
        final String password = request.getBodyValue("password");
        final String email = request.getBodyValue("email");

        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);

        log.info("user : " + newUser);
        return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI);
    }

    private HandlerResult createHandlerResult(final HttpStatusCode statusCode, final String redirectUri) {
        final Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Location", redirectUri);
        return new HandlerResult(statusCode, responseHeader, "");
    }
}
