package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Cookie {

    private final Map<String, String> cookies;

    private Http11Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Http11Cookie from(String cookies) {
        if (cookies.isBlank()) {
            return new Http11Cookie(Map.of());
        }

        Map<String, String> cookieMap = Arrays.stream(cookies.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1])); // ArrayIndexOutOfBoundsException

        return new Http11Cookie(cookieMap);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }
}
