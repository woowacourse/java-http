package org.apache.coyote.http11.common;

public class HttpVersion {

    private final String value;

    public HttpVersion(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
