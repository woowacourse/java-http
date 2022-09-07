package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> refinedCookies = Arrays.stream(cookies.split("; "))
                .map(cookie -> cookie.split(":"))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));

        return new HttpCookie(refinedCookies);
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public boolean validate() {
        return !cookies.containsKey("JSESSIONID");
    }
}
