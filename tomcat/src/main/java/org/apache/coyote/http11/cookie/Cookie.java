package org.apache.coyote.http11.cookie;

public class Cookie {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String COOKIE_NAME_VALUE_DELIMITER = "=";

    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Cookie createSessionCookie(String value) {
        return new Cookie(SESSION_COOKIE_NAME, value);
    }

    public String getCookieString() {
        return name + COOKIE_NAME_VALUE_DELIMITER + value;
    }
}
