package org.apache.coyote.http11.header;

public class HttpCookie {

    private static final String DELIMITER = "=";

    private final String name;
    private final String value;

    public HttpCookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String toHeaderValue() {
        return name + DELIMITER + value;
    }

    public String getValue() {
        return value;
    }
}
