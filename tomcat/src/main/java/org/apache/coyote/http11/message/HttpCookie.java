package org.apache.coyote.http11.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HttpCookie {
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final HttpHeaders headers) {
        final Map<String, String> cookies = getCookies(headers);
        return new HttpCookie(cookies);
    }

    public static HttpCookie of(final String name, final String cookieValue) {
        final Map<String, String> cookies = new HashMap<>();
        cookies.put(name, cookieValue);
        return new HttpCookie(cookies);
    }

    public boolean contains(final String name) {
        return cookies.containsKey(name);
    }

    public String toHeaderCookie() {
        StringJoiner joiner = new StringJoiner("; ");
        cookies.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }

    public String getJsessionId() {
        return cookies.get("JSESSIONID");
    }

    private static Map<String, String> getCookies(final HttpHeaders headers) {
        final Map<String, String> cookies = new HashMap<>();
        final List<String> cookieValues = headers.getHeaders("Cookie");
        if (cookieValues == null || cookieValues.isEmpty()) {
            return cookies;
        }

        for (String cookieValue : cookieValues) {
            parse(cookieValue, cookies);
        }

        return cookies;
    }

    private static void parse(String cookieValue, Map<String, String> cookies) {
        final String[] pairs = cookieValue.split(";");
        for (String pair : pairs) {
            final int index = pair.indexOf("=");

            addCookie(cookies, pair, index);
        }
    }

    private static void addCookie(Map<String, String> cookies, String pair, int index) {
        if (index != -1) {
            final String key = pair.substring(0, index).trim();
            final String value = pair.substring(index + 1).trim();
            cookies.put(key, value);
        }
    }
}
