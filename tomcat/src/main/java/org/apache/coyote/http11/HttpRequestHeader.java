package org.apache.coyote.http11;

import java.util.Map;
import java.util.Objects;

public record HttpRequestHeader(
        RequestLine firstLine,
        Map<String, String> header,
        HttpCookie cookie
) {
    public String path() {
        Objects.requireNonNull(firstLine);

        return firstLine.path();
    }

    public boolean hasContain(String string) {
        return header.containsKey(string);
    }
}
