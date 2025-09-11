package org.apache.coyote.http11.dto.request;

import java.util.Map;

public record Cookie(
        Map<String, String> cookies
) {

    public String getCookie(final String name) {
        return cookies.get(name);
    }

    public boolean containsCookie(final String name) {
        return cookies.containsKey(name);
    }
}
