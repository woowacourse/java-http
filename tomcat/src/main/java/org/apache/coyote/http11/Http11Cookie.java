package org.apache.coyote.http11;

public class Http11Cookie {

    private static final String SESSION_COOKIE = "JSESSIONID";

    private final String key;
    private final String value;

    public Http11Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Http11Cookie sessionCookie(String value) {
        return new Http11Cookie(SESSION_COOKIE, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
