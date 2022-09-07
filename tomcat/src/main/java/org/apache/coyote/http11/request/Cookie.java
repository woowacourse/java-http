package org.apache.coyote.http11.request;

public class Cookie {

    private final String value;

    public Cookie(String value) {
        this.value = value;
    }

    public Cookie() {
        this("");
    }

    public static Cookie empty() {
        return new Cookie();
    }

    public static Cookie from(String value) {
        return new Cookie(value);
    }
}
