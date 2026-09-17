package org.apache.coyote.http11;

public class Cookie {

    private final String key;
    private final String value;
    private final Integer maxAge;

    private Cookie(String key, String value) {
        this(key, value, null);
    }

    private Cookie(String key, String value, Integer maxAge) {
        this.key = key;
        this.value = value;
        this.maxAge = maxAge;
    }

    public static Cookie ofJSessionId(String sessionId) {
        return new Cookie("JSESSIONID", sessionId);
    }

    public static Cookie expiredJSessionId() {
        return new Cookie("JSESSIONID", "", 0);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        if (maxAge != null) {
            return key + "=" + value + "; Max-Age=" + maxAge;
        }
        return key + "=" + value;
    }
}
