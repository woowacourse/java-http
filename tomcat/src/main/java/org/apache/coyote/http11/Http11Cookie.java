package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Cookie {

    private Map<String, String> cookies;

    public Http11Cookie(String cookies) {
        parseCookies(cookies);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public boolean isCookieExist(String cookieName) {
        return cookies.containsKey(cookieName);
    }

    private void parseCookies(String cookies) {
        if (cookies == null) {
            this.cookies = Map.of();

            return;
        }
        this.cookies = Arrays.stream(cookies.split(";", 2))
                .map(cookie -> cookie.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> arr[0].trim(), arr -> arr[1].trim()
                ));
    }
}
