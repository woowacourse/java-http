package org.apache.coyote.http11;

public class Cookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final String name;
    private final String value;

    private Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Cookie of(String name, String value) {
        return new Cookie(name, value);
    }

    public static Cookie session(String value) {
        return new Cookie(JSESSIONID, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getCookieString() {
        return String.format("%s=%s", name, value);
    }
}
