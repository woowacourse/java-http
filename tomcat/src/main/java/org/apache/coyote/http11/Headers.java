package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.HttpResponse.BLANK;
import static org.apache.coyote.http11.HttpResponse.CRLF;

public class Headers {

    private final Map<String, String> keyValues;

    public Headers() {
        this(new HashMap<>());
    }

    public Headers(final Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public String join() {
        return keyValues.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + BLANK)
                .collect(Collectors.joining(CRLF));
    }

    public void put(String key, String value) {
        keyValues.put(key, value);
    }

    public String get(String key) {
        if (keyValues.isEmpty()) {
            return null;
        }
        return keyValues.get(key);
    }


}
