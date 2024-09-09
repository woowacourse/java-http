package org.apache.coyote.http11;

import java.util.Map;

public record HttpHeader(Map<String, String> headers) {

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append("\r\n"));
        return stringBuilder.toString();
    }
}
