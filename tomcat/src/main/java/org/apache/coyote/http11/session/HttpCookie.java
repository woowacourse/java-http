package org.apache.coyote.http11.session;

public class HttpCookie {

    private static final String COOKIE_NAME = "JSESSIONID";

    private final String cookieHeader;

    public HttpCookie(String cookieHeader) {
        this.cookieHeader = cookieHeader;
    }

    public String getJSessionId() {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] nameAndValue = cookie.trim().split("=", 2);

            if (nameAndValue.length == 2
                    && nameAndValue[0].equals(COOKIE_NAME)
                    && !nameAndValue[1].isBlank()) {
                return nameAndValue[1];
            }
        }

        return null;
    }

    public String createSetCookieHeaderForResponse(String sessionId) {
        return "Set-Cookie: " + COOKIE_NAME + "=" + sessionId;
    }
}
