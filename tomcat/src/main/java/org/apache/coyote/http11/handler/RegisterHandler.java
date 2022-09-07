package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.authorization.SessionManager.SESSION_MANAGER;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.authorization.Session;
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

            InMemoryUserRepository.save(new User(account, password, email));
            final User savedUser = InMemoryUserRepository.findByAccount(account).get();
            log.info("user : " + savedUser);

            final String jSessionId = String.valueOf(UUID.randomUUID());
            createSession(jSessionId, savedUser);

            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI, jSessionId);
        } catch (IllegalArgumentException e) {
            return createHandlerResult(HttpStatusCode.FOUND, FAIL_REDIRECT_URI);
        }
    }

    private void createSession(final String jSessionId, final User user) {
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
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
