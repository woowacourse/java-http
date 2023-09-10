package org.apache.coyote.http11.response;

public class ResponseHeader {

    private final String name;
    private final String value;

    public ResponseHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String toMessage() {
        return String.format("%s: %s ", name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
