package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    public void addValue(final String key, final String value) {
        values.put(key, value);
    }

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }
}
