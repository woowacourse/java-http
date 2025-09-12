package org.apache.coyote.http11.request_response;

import java.util.Objects;

public final class HttpHeader {
    private final String name;
    private final String value;

    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean nameEquals(String other) {
        return name.equalsIgnoreCase(other);
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }
}
