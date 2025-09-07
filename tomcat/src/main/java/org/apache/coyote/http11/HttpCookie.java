package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> map = new HashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(String cookies) {
        String[] cookieSplit = cookies.split("; ");

        Arrays.stream(cookieSplit)
                .forEach(cookie -> {
                    String[] split = cookie.split("=");
                    map.put(split[0], split[1]);
                });
    }

    public String toSetCookieString() {
        return String.join("; ",
                map.entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .toList());
    }

    public void add(String key, String value) {
        map.put(key, value);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public String getValue(String key) {
        return map.get(key);
    }
}
