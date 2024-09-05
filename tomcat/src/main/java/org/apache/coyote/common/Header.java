package org.apache.coyote.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Header {

    private static final String PAIR_DELIMITER = "=";

    private final Map<String, String> header = new HashMap<>();

    public Header(List<String> header) {
        Objects.requireNonNull(header);

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

        header.put(key, value);
    }

    public Optional<String> get(String key) {
        Objects.requireNonNull(key);

        return Optional.ofNullable(header.get(key));
    }
}
