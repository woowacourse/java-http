package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;

public class RegisterHandler extends Handler {

    static final RegisterHandler REGISTER_HANDLER = new RegisterHandler();

    private static String JSESSIONID = "JSESSIONID";
    private static String SUCCESS_REDIRECT_URI = "/index.html";
    private static String FAIL_REDIRECT_URI = "/401.html";

    private RegisterHandler() {
    }

    @Override
    public HandlerResult handle(HttpRequest request) throws IOException {
        try {
            if (request.getCookieValue(JSESSIONID) != null) {
                throw new IllegalArgumentException("이미 로그인이 되어있습니다.");
            }

            final String account = request.getBodyValue("account");
            final String password = request.getBodyValue("password");
            final String email = request.getBodyValue("email");

            final User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);

            log.info("user : " + newUser);
            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI, String.valueOf(UUID.randomUUID()));
        } catch (IllegalArgumentException e) {
            return createHandlerResult(HttpStatusCode.FOUND, FAIL_REDIRECT_URI);
        }
    }

    private HandlerResult createHandlerResult(final HttpStatusCode statusCode, final String redirectUri,
                                              final String jSessionId) {
        final Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Location", redirectUri);
        responseHeader.put("Set-Cookie", JSESSIONID + "=" + jSessionId);
        return new HandlerResult(statusCode, responseHeader, "");
    }

    private HandlerResult createHandlerResult(final HttpStatusCode statusCode, final String redirectUri) {
        final Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Location", redirectUri);
        return new HandlerResult(statusCode, responseHeader, "");
    }
}
