package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values = new LinkedHashMap<>();

    public HttpCookie(String rawCookie) {
        if (rawCookie.isEmpty()) {
            return;
        }

        for (String cookie : rawCookie.split(";")) {
            String[] nameAndValue = cookie.trim().split("=", 2);
            values.put(nameAndValue[0], nameAndValue[1]);
        }
    }

    public boolean hasSessionId() {
        return values.containsKey("JSESSIONID");
    }

    public String getSessionId() {
        return values.get("JSESSIONID");
    }
}
