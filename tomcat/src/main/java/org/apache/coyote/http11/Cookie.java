package org.apache.coyote.http11;

public class Cookie {
    private static final String COOKIE_SEPARATOR = "=";
    private final String name;
    private final String value;

    public Cookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static Cookie of(final String cookie) {
        final String[] tokens = cookie.split(COOKIE_SEPARATOR);
        if (tokens.length == 2) {
            return new Cookie(tokens[0], tokens[1]);
        }
        throw new IllegalArgumentException("올바른 쿠키 형식이 아닙니다.");
    }

    public static String ofJSessionId(final String sessionId) {
        return "JSESSIONID=" + sessionId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
