package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Header {

    private static final String PAIR_DELIMITER = ":";

    private final Map<String, String> header = new HashMap<>();

    public Header(List<String> header) {
        parseHeader(header);
    }

    private void parseHeader(List<String> headers) {
        for (String pair : headers) {
            if (pair.contains(PAIR_DELIMITER)) {
                String[] split = pair.split(PAIR_DELIMITER);
                putIfValidPair(split);
            }
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            return;
        }
        String key = keyValuePair[0].trim();
        String value = keyValuePair[1].trim();

        append(key, value);
    }

    public void append(HttpHeaderKey key, String value) {
        append(key.getName(), value);
    }

    public void append(String key, String value) {
        header.put(key, value);
    }

    public Optional<String> get(HttpHeaderKey key) {
        return get(key.getName());
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(header.get(key));
    }

    public Map<String, String> getHeader() {
        return Collections.unmodifiableMap(header);
    }
}
