package org.apache.coyote.http11;

import java.util.Objects;

public class HttpBody {
    private final String value;

    public HttpBody(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return value.getBytes().length;
    }
}
