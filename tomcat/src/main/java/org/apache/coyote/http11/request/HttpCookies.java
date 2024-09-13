package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private final Map<String, String> cookies;

    public HttpCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(String cookie) {
        Map<String, String> cookies = Arrays.stream(cookie.split(";"))
                .map(c -> c.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        return new HttpCookies(cookies);
    }

    public boolean isExist(String name) {
        return cookies.containsKey(name);
    }

    public String get(String name) {
        return cookies.get(name);
    }
}
