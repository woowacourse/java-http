package org.apache.coyote.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookies {

    private static final String DELIMITER = "\r\n";
    private final Map<String, String> cookies = new LinkedHashMap<>();

    public HttpCookies() {
    }

    public HttpCookies(final Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public static HttpCookies from(final String cookieHeader) {
        if (cookieHeader == null) {
            return new HttpCookies(Collections.emptyMap());
        }

        final Map<String, String> cookieMap = Arrays.stream(cookieHeader.split(";"))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0].trim(), cookie -> cookie[1].trim()));

        return new HttpCookies(cookieMap);
    }

    public void save(final String cookie, final String value) {
        cookies.put(cookie, value);
    }

    public String ofSessionId(final String sessionId) {
        return cookies.get(sessionId);
    }

    public String toResponse() {
        if (cookies.isEmpty()) {
            return "";
        }

        final List<String> cookieString = cookies.keySet()
                .stream()
                .map(name -> "Set-Cookie: " + name + "=" + cookies.get(name) + " ")
                .collect(Collectors.toList());

        return String.join(DELIMITER, cookieString) + DELIMITER;
    }
}
