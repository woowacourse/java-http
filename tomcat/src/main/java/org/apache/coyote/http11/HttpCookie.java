package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public String getValue(String input) {
        return values.get(input);
    }

    public boolean hasValue(String input) {
        return values.containsKey(input);
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();
        values.entrySet()
                .forEach(entry -> response.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append(" ")
                );
        return response.toString();
    }
}
