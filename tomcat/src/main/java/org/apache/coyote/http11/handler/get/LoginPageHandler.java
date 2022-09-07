package org.apache.coyote.http11.handler.get;

import static org.apache.coyote.http11.authorization.SessionManager.SESSION_MANAGER;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.handler.HandlerResult;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginPageHandler extends HandlerForGetRequest {

    public static final LoginPageHandler LOGIN_PAGE_HANDLER = new LoginPageHandler();
    private static String JSESSIONID = "JSESSIONID";

    private LoginPageHandler() {
    }

    @Override
    public HandlerResult handle(HttpRequest request) throws IOException {
        String sessionId;
        if ((sessionId = request.getCookieValue(JSESSIONID)) != null &&
                SESSION_MANAGER.findSession(sessionId).isPresent()) {
            return new HandlerResult(HttpStatusCode.FOUND, createRedirectResponse("/index.html"), "");
        }

        final String uri = request.getUri();
        return new HandlerResult(HttpStatusCode.OK, createResponseHeader(uri),
                getResponseBody(uri));
    }

    private Map<String, String> createRedirectResponse(final String redirectUri) {
        final Map<String, String> header = new HashMap<>();
        header.put("Location", redirectUri);
        return header;
    }
}
