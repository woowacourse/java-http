package org.apache.coyote.http11;

import java.util.Map;
import java.util.StringJoiner;

public record HttpHeader(Map<String, String> headers) {

    public String toString() {
        StringJoiner joiner = new StringJoiner("\r\n");
        headers.forEach((key, value) -> joiner.add(key + ": " + value));
        return joiner.toString();
    }
}
