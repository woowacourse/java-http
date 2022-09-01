package org.apache.coyote.http11.request;

public class URL {

    private final String value;

    public URL(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
