package org.apache.coyote.http11;

public class Cookie {

    private final String key;
    private final String value;

    private Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie ofJSessionId(String sessionId) {
        return new Cookie("JSESSIONID", sessionId);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return key + "=" + value;
    }
}
