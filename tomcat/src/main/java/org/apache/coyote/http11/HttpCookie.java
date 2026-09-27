package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String rawCookie) {
        Map<String, String> parsed = new HashMap<>();

        for (String pair : rawCookie.split(";")) {
            String trimmed = pair.strip();
            if (trimmed.isBlank()) {
                continue;
            }
            String[] nameAndValue = trimmed.split("=", 2);
            String name = nameAndValue[0];
            String value = "";
            if (nameAndValue.length > 1) {
                value = nameAndValue[1];
            }
            parsed.put(name, value);
        }
        return new HttpCookie(parsed);
    }

    public String getJSessionId() {
        return cookies.getOrDefault(JSESSIONID, "");
    }

    public String toCookieLine() {
        if (cookies.isEmpty()) {
            return "";
        }

        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
