package org.apache.coyote.http11.response;

import java.util.StringJoiner;

public class ResponseCookie {

    private final String name;
    private final String value;

    public ResponseCookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String toHeaderString() {
        StringJoiner joiner = new StringJoiner("; ");
        joiner.add(name + "=" + value);
        return joiner.toString();
    }
}
