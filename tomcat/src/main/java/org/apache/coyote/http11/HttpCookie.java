package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    private HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie of(Map<String, String> cookies){
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.putAll(cookies);
        return httpCookie;
    }

    public String getAttribute(String name){
        return cookies.get(name);
    }

    public boolean containsAttribute(String name){
        return cookies.containsKey(name);
    }

    private void putAll(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public String toMessage() {
        return cookies.entrySet().stream()
            .map(it -> it.getKey() + "=" + it.getValue())
            .collect(Collectors.joining(";"));
    }
}

