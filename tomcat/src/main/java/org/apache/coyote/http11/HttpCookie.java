package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie ofJSessionId(String sessionId) {
        return new HttpCookie(Map.of("JSESSIONID", sessionId));
    }

    public String getValue(String input) {
        return values.get(input);
    }

    public boolean hasValue(String input) {
        return values.containsKey(input);
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();
        values.forEach((key, value) -> response.append(key)
                .append("=")
                .append(value)
                .append(" "));
        return response.toString();
    }
}
