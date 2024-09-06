package org.apache.catalina.auth;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final Map<String, String> cookies = new ConcurrentHashMap<>();
    private static final String AUTH_COOKIE_KEY = "JSESSIONID";

    public static String ofJSessionId(String id) {
        saveAuthCookie(id);
        return cookiesToString();
    }

    public static void setCookies(String setCookies) {
        if (setCookies == null) {
            return;
        }
        Map<String, String> map = Arrays.stream(setCookies.split(";"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
        cookies.putAll(map);
    }

    public static String getId() {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return cookies.get(AUTH_COOKIE_KEY);
        }
        return UUID.randomUUID().toString();
    }

    public static void saveAuthCookie(String id) {
        if (cookies.containsKey(AUTH_COOKIE_KEY)) {
            return;
        }
        HttpCookie.cookies.put(AUTH_COOKIE_KEY, id);
    }

    public static String cookiesToString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    private HttpCookie() {}

}
