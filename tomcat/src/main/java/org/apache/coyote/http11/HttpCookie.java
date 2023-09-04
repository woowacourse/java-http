package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public static HttpCookie ofJSessionId(final String jSessionId) {
        final Map<String, String> values = new HashMap<>();
        values.put("JSESSIONID", jSessionId);

        return new HttpCookie(values);
    }

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public String getValues() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String key : values.keySet()) {
            stringBuilder.append(key)
                    .append("=")
                    .append(values.get(key))
                    .append(" ");
        }
        return stringBuilder.toString();
    }

}
