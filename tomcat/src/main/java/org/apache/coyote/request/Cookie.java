package org.apache.coyote.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final Map<String, String> cookies = new LinkedHashMap<>();

    private Cookie() {
    }

    public static Cookie from(String requestCookie) {
        for (final String oneCookie : requestCookie.split(" ")) {
            if (!oneCookie.contains("=")) {
                continue;
            }
            final String[] cookie = oneCookie.split("=", 2);
            cookies.put(cookie[0], cookie[1]);
        }
        return new Cookie();
    }

    public static String ofJSessionId(final String key) {
        if (cookies.containsKey(JSESSIONID)) {
            final String cookie = cookies.get(key);
            return JSESSIONID + "=" + cookie;
        }
        return JSESSIONID + "=" + key;
    }

    public boolean hasJsessionid() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJsessionid() {
        return cookies.get(JSESSIONID);
    }

    @Override
    public String toString() {
        return "Cookie: " + cookies.keySet()
                .stream()
                .map(key -> key + "=" + cookies.get(key))
                .collect(Collectors.joining("; "));
    }
}
