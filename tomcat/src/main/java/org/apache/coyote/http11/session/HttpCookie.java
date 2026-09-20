package org.apache.coyote.http11.session;

import java.util.UUID;

public class HttpCookie {

    private static final String COOKIE_NAME = "JSESSIONID";

    private final String cookieHeader;

    public HttpCookie(String cookieHeader) {
        this.cookieHeader = cookieHeader;
    }

    public boolean hasJSessionId() {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return false;
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] nameAndValue = cookie.trim().split("=");

            boolean isCookieNameExist = nameAndValue[0].equals(COOKIE_NAME);
            boolean isCookieIDNotNull = !nameAndValue[1].isBlank();

            if (isCookieNameExist && isCookieIDNotNull) {
                return true;
            }
        }

        return false;
    }

    public String createSetCookieHeaderForResponse() {
        return "Set-Cookie: " + COOKIE_NAME + "=" + UUID.randomUUID();
    }
}
