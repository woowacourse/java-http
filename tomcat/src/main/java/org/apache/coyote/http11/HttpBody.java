package org.apache.coyote.http11;

public class HttpBody {
    private final String value;

    public HttpBody(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
