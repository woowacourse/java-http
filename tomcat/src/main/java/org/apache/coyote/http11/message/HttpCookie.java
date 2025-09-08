package org.apache.coyote.http11.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HttpCookie {
    private final Map<String, String> cookies = new HashMap<>();

    private HttpCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public static HttpCookie from(HttpHeaders header) {
        Map<String, String> cookies = parseHeader(header);
        return new HttpCookie(cookies);
    }

    public static HttpCookie of(String key, String value) {
        Map<String, String> cookies = new HashMap<>();
        cookies.put(key, value);
        return new HttpCookie(cookies);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public boolean contains(String name) {
        return cookies.containsKey(name);
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(cookies);
    }

    private static Map<String, String> parseHeader(HttpHeaders header) {
        Map<String, String> cookies = new HashMap<>();
        List<String> cookieStrings = header.get("Cookie");
        if (cookieStrings == null || cookieStrings.isEmpty()) {
            return cookies;
        }

        for (String cookieString : cookieStrings) {
            String[] pairs = cookieString.split(";");
            for (String pair : pairs) {
                addPairToCookies(pair, cookies);
            }
        }

        return cookies;
    }

    private static void addPairToCookies(String pair, Map<String, String> cookies) {
        int idx = pair.indexOf('=');
        if (idx != -1) {
            String key = pair.substring(0, idx).trim();
            String value = pair.substring(idx + 1).trim();
            cookies.put(key, value);
        }
    }

    public String toHeaderString() {
        StringJoiner joiner = new StringJoiner("; ");
        cookies.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }
}
