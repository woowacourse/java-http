package org.apache.coyote.http11.request;

public class RequestBody {

    private final String value;

    public RequestBody(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
