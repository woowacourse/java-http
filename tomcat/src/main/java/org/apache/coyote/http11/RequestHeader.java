package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;
    private final Map<String, Cookie> cookies;

    public RequestHeader(final Map<String, String> headers, final Map<String, Cookie> cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static RequestHeader from(String headerPart) {
        String[] headerLines = headerPart.split("\r\n");

        Map<String, String> headers = new HashMap<>();
        Map<String, Cookie> cookies = new HashMap<>();
        for (int i = 1; i < headerLines.length; i++) {
            String[] kv = headerLines[i].split(":", 2);
            String name = kv[0].trim();
            String value = kv[1].trim();
            if (name.equals("Cookie")) {
                for (String rawCookie : value.split(";")) {
                    String[] cookieParts = rawCookie.split("=", 2);
                    Cookie cookie = new Cookie(cookieParts[0].trim(), cookieParts[1].trim());
                    cookies.put(cookie.getName(), cookie);
                }
            }
            headers.put(name, value);
        }

        return new RequestHeader(headers, cookies);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }

    public Cookie getSessionCookie() {
        return cookies.get("JSESSIONID");
    }
}
