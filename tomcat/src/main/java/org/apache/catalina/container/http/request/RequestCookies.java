package org.apache.catalina.container.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.container.http.Cookie;
import org.apache.catalina.container.http.value.HttpHeader;

public class RequestCookies {

    private final Map<String, Cookie> cookies;

    public RequestCookies(List<String> headerLines) {
        this.cookies = parseCookies(headerLines);
    }

    public boolean containKey(String key) {
        return cookies.containsKey(key);
    }

    public Cookie getValue(String key) {
        return cookies.get(key);
    }

    private Map<String, Cookie> parseCookies(List<String> headerLines) {
        Map<String, Cookie> cookieRead = new HashMap<>();

        Optional<String> cookieHeaderOptional = getCookieHeader(headerLines);
        if (cookieHeaderOptional.isEmpty()) {
            return cookieRead;
        }

        String cookieHeaderLine = cookieHeaderOptional.get();
        String cookieHeaderValue = List.of(cookieHeaderLine.split(":")).getLast();

        List<String> cookieLines = List.of(cookieHeaderValue.split(";"));
        for (String cookieLine : cookieLines) {
            int index = cookieLine.indexOf("=");
            String key = cookieLine.substring(0, index).trim();
            String value = cookieLine.substring(index + 1).trim();
            cookieRead.put(key, new Cookie(key, value));
        }
        return cookieRead;
    }

    private Optional<String> getCookieHeader(List<String> headerLines) {
        return headerLines.stream()
                .filter(line -> line.toLowerCase().startsWith(HttpHeader.COOKIE.getValue()))
                .findFirst();
    }
}
