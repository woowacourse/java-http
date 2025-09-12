package org.apache.coyote.http11.request;

import java.io.IOException;
import org.apache.coyote.http11.SessionManager;

public class CookieSessionAuthenticator {

    private final HttpRequest httpRequest;
    private final SessionManager sessionManager;

    public CookieSessionAuthenticator(HttpRequest httpRequest, SessionManager sessionManager) {
        this.httpRequest = httpRequest;
        this.sessionManager = sessionManager;
    }

    public boolean containsCookie() {
        return httpRequest.containsCookie();
    }

    public void validateUserCookie() throws IOException {
        String cookie = httpRequest.getCookie();

        if (!sessionManager.isExistSessionId(cookie)) {
            throw new IllegalArgumentException("[ERROR] no such session Id");
        }
    }
}
