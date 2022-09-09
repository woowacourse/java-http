package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    private HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie from(Map<String, String> cookies) {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.putAll(cookies);
        return httpCookie;
    }

    public void putAll(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public boolean containsAttribute(String name) {
        return cookies.containsKey(name);
    }

    public String getAttributeOrDefault(String name, String defaultValue) {
        return cookies.getOrDefault(name, defaultValue);
    }

    public String toMessage() {
        return cookies.entrySet().stream()
            .map(it -> it.getKey() + "=" + it.getValue())
            .collect(Collectors.joining(";"));
    }
}
