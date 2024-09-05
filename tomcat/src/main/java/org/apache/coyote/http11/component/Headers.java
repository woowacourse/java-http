package org.apache.coyote.http11.component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Headers {

    private static final String PARAMETER_DELIMITER = "\r\n";
    private static final String KEY_VALUE_DELIMITER = ":";

    private final Map<String, String> values = new ConcurrentHashMap<>();

    public Headers(final String plaintext) {
        List.of(plaintext.split(PARAMETER_DELIMITER))
                .forEach(this::consume);
    }

    private void consume(final String param) {
        Objects.requireNonNull(param);
        final var pair = List.of(param.split(KEY_VALUE_DELIMITER, 2));
        if (pair.size() < 2) {
            throw new IllegalArgumentException("해더 올바르지 않은 파라미터 갯수");
        }
        values.put(pair.get(0).toLowerCase(), pair.get(1));
    }

    public String get(final String key) {
        return values.get(key.toLowerCase());
    }
}
