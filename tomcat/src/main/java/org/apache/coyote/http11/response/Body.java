package org.apache.coyote.http11.response;

public class Body {

    public static final Body EMPTY_BODY = new Body("");

    private final String value;

    public Body(final String value) {
        this.value = value;
    }

    public String toMessage() {
        return String.format("%n%s", value);
    }

    public String getValue() {
        return value;
    }
}
