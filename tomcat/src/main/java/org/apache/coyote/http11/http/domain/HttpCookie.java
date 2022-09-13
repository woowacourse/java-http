package org.apache.coyote.http11.http.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookies) {
        if (Objects.isNull(cookies)) {
            return new HttpCookie(new HashMap<>());
        }
        return new HttpCookie(Arrays.stream(cookies.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1])));
    }

    public boolean containsJSESSIONID() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getCookie(final String cookieKey) {
        return cookies.get(cookieKey);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
