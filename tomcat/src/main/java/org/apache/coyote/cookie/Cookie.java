package org.apache.coyote.cookie;

public class Cookie {

    private static final String COOKIE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String JSESSIONID = "JSESSIONID";

    private final String key;
    private final String value;

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Cookie from(String cookie) {
        final String[] splitCookie = cookie.split(COOKIE_DELIMITER);

        return new Cookie(splitCookie[KEY_INDEX], splitCookie[VALUE_INDEX]);
    }

    public static Cookie empty() {
        return new Cookie("", "");
    }

    public static Cookie ofJSessionId(String id) {
        return new Cookie(JSESSIONID, id);
    }

    public boolean isEmpty() {
        return key.equals("");
    }

    public boolean isSameKey(String key) {
        return this.key.equals(key);
    }

    public String toHeaderFormat() {
        return this.key + "=" + this.value;
    }

    public boolean isJSessionCookie() {
        return key.equals(JSESSIONID);
    }

    public String getValue() {
        return value;
    }
}
