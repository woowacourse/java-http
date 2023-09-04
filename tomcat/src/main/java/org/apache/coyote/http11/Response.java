package org.apache.coyote.http11;

public class Response {
    private final String value;

    public Response(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
