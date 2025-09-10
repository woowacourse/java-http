package org.apache.coyote.http11.domain;

import java.util.HashMap;
import java.util.Map;

public record HttpCookies(Map<String, String> values) {

    private static final String JSESSIONID = "JSESSIONID";

    public HttpCookies() {
        this(new HashMap<>());
    }

    public String getJsessionid() {
        return values.getOrDefault(JSESSIONID, null);
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void appendSetCookieHeaders(StringBuilder sb) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            sb.append("Set-Cookie: ")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(" \r\n");
        }
    }
}
