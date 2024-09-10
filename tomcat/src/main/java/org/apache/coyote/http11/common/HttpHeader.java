package org.apache.coyote.http11.common;

import java.util.Map;
import java.util.OptionalInt;
import java.util.StringJoiner;

public record HttpHeader(Map<String, String> headers) {

    private static final String CONTENT_LENGTH = "Content-Length";

    public String toString() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        headers.forEach((key, value) -> joiner.add(key + ": " + value));
        return joiner.toString();
    }

    public OptionalInt getContentLength() {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(headers.get(CONTENT_LENGTH)));
    }
}
