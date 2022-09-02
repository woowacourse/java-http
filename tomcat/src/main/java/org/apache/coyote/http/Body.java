package org.apache.coyote.http;

public class Body {

    private String value;

    public Body(final String bodyValue) {
        this.value = bodyValue;
    }

    public String getValue() {
        return value;
    }
}
