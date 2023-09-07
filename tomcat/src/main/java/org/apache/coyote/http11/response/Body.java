package org.apache.coyote.http11.response;

public class Body {

    private final String value;

    public Body(final String value) {
        this.value = value;
    }

    public String toMessage() {
        return "\r\n" + value;
    }

    public String getValue() {
        return value;
    }
}
