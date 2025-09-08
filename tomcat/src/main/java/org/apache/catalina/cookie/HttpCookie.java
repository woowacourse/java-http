package org.apache.catalina.cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = ";";
    private static final String PAIR_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(String cookieHeader) {
        this.cookies = parseCookieHeader(cookieHeader);
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    private Map<String, String> parseCookieHeader(String cookieHeader) {
        if (cookieHeader == null) {
            return new HashMap<>();
        }

        return Arrays.stream(cookieHeader.split(COOKIE_DELIMITER))
                .filter(pair -> pair.contains(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        pair -> pair.substring(0, pair.indexOf(PAIR_DELIMITER)),
                        pair -> pair.substring(pair.indexOf(PAIR_DELIMITER) + 1)
                ));
    }
}
