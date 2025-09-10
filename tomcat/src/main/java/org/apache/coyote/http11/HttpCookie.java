package org.apache.coyote.http11;

import jakarta.servlet.http.Cookie;
import java.util.UUID;

public class HttpCookie {

    private static final String COOKIE_NAME = "JSESSIONID";

    public static Cookie createCookie() {
        final String sessionId = UUID.randomUUID().toString();
        return new Cookie(COOKIE_NAME, sessionId);
    }

}


