package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private Map<String, String> attributes =  new HashMap<>();

    public HttpCookie(String cookieHeader) {
        this.attributes = parse(cookieHeader);
    }

    private Map<String, String> parse(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookies;
        }

        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.isEmpty()) {
                continue;
            }

            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue.length > 1 ? keyValue[1].trim() : "";
            cookies.put(key, value);
        }

        return cookies;
    }

    public String serialize() {
        return attributes.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

}
