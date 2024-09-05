package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(String rawCookies) {
        this.cookies = parse(rawCookies);
    }

    private Map<String, String> parse(String rawCookies) {
        return Arrays.stream(rawCookies.split("; "))
                .map(cookie -> cookie.split("="))
                .filter(data -> data.length == 2)
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }
}
