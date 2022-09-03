package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.exception.NoSuchHeaderException;

public class Headers {

    private final LinkedHashMap<String, String> values;

    public Headers(final LinkedHashMap<String, String> values) {
        this.values = values;
    }

    public void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public String findByHeaderKey(final String key) {
        if (!values.containsKey(key)) {
            throw new NoSuchHeaderException();
        }

        return values.get(key);
    }

    public LinkedHashMap<String, String> getValues() {
        return new LinkedHashMap<>(values);
    }
}
