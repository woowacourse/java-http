package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void add(String line) {
        int colonIndex = line.indexOf(":");

        if (colonIndex == -1) {
            return;
        }

        String name = line.substring(0, colonIndex)
                .trim()
                .toLowerCase();
        String value = line.substring(colonIndex + 1).trim();

        headers.put(name, value);
    }

    public String get(String name) {
        return headers.get(name.toLowerCase());
    }

    public int contentLength() {
        return Integer.parseInt(headers.getOrDefault("content-length", "0"));
    }

    public Optional<Cookie> getCookie(String name) {
        String cookieHeader = get("cookie");

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return Optional.empty();
        }

        String[] cookiePairs = cookieHeader.split(";");

        for (String pair : cookiePairs) {
            Optional<Cookie> parsed = Cookie.parse(pair);
            if (parsed.isEmpty()) {
                continue;
            }

            if (name.equals(parsed.get().name())) {
                return parsed;
            }
        }

        return Optional.empty();
    }
}
