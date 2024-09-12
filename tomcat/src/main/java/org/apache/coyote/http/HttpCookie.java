package org.apache.coyote.http;

public class HttpCookie implements HttpComponent {

    public static final String JSESSIONID = "JSESSIONID";

    private final String name;
    private final String value;

    public HttpCookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpCookie ofJSessionId(final String value) {
        return new HttpCookie(JSESSIONID, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String asString() {
        return name + KEY_VALUE_SEPARATOR + value;
    }
}
