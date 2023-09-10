package org.apache.coyote.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String JSESSIONID = "JSESSIONID";
    private final Map<String, String> cookies;

    public Cookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(String requestCookie) {
        final Map<String, String> cookies = new LinkedHashMap<>();

        for (final String oneCookie : requestCookie.split(" ")) {
            if (!oneCookie.contains("=")) {
                break;
            }
            final String[] cookie = oneCookie.split("=", 2);
            cookies.put(cookie[0], cookie[1]);
        }
        return new Cookie(cookies);
    }

    public boolean hasJsessionid() {
        return cookies.containsKey(JSESSIONID);
    }

    @Override
    public String toString() {
        return "Cookie: " + cookies.keySet()
                .stream()
                .map(key -> key + ":" + cookies.get(key))
                .collect(Collectors.joining(" "));
    }
}
