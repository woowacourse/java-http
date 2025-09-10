package org.apache.coyote.http11.dto;

import java.util.Map;

public record HttpCookie(
        Map<String, String> cookieMap
) {

    public String getCookie(final String name) {
        return cookieMap.get(name);
    }

    public boolean containsCookie(final String name) {
        return cookieMap.containsKey(name);
    }
}
