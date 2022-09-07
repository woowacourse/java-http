package org.apache.coyote.http11.web;

public class Cookie {

    private final String key;
    private final String value;

    public Cookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie ofJSessionId() {
        return new Cookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    public String toPair() {
        return key + "=" + value;
    }
}
