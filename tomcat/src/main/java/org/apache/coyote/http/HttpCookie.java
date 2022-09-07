package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        return new HttpCookie(Arrays.stream(cookieHeader.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(e -> e[0], e -> e[1])));
    }

    public boolean hasJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSessionId() {
        if (hasJSessionId()) {
            return cookies.get("JESESSIONID");
        }
        return "";
    }
}
