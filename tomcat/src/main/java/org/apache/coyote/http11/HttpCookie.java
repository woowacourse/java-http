package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = parseCookie(cookieHeader);
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
        cookies.put("JSESSIONID", generateJSESSIONID());
    }

    public static HttpCookie fromJSESSIONID(String id) {
        return new HttpCookie("JSESSIONID=" + id);
    }

    private String generateJSESSIONID() {
        return UUID.randomUUID().toString();
    }

    private Map<String, String> parseCookie(String cookieHeader) {

        final List<String[]> result = Arrays.stream(cookieHeader.split(";"))
            .map(it -> it.trim().split("="))
            .collect(Collectors.toList());

        return result.stream()
            .collect(Collectors
                .toMap(cookie -> cookie[0], cookie -> cookie[1], (a, b) -> b));
    }

    public String getCookieValue(String cookieName) {
        return cookies.get(cookieName);
    }

    public String getCookieHeader() {
        return "JSESSIONID" + "=" + cookies.get("JSESSIONID");
    }
}
