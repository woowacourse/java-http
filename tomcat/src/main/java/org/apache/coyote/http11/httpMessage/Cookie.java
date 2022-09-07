package org.apache.coyote.http11.httpmessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie of(String key, String value) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(key, value);
        return new Cookie(headers);
    }

    @Override
    public String toString() {
        return cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; ")) + " ";
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
