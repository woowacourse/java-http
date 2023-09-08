package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.converter.HeaderStringConverter;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    public void addCookie(final String name, final String value) {
        values.put(name, value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String toMessageFormat() {
        return HeaderStringConverter.toSetCookie(values);
    }

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }
}
