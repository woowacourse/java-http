package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.HttpCookie;

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
