package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] keyValue = cookie.trim().split("=", 2);

            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    private HttpCookie(String name, String value) {
        cookies.put(name, value);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public static HttpCookie ofJSessionId() {
        return new HttpCookie(JSESSIONID, UUID.randomUUID().toString());
    }

    public String toHeaderValue() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if (result.length() > 0) {
                result.append("; ");
            }

            result.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }

        return result.toString();
    }
}
