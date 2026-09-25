package org.apache.coyote.http11;

public class Cookie {

    public static final String JSESSIONID = "JSESSIONID";

    private static final String KEY_VALUE_SEPARATOR = "=";

    private final String name;
    private final String value;

    public Cookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static Cookie ofJSessionId(final String sessionId) {
        return new Cookie(JSESSIONID, sessionId);
    }

    public String toMessage() {
        return name + KEY_VALUE_SEPARATOR + value;
    }
}
