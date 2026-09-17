package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParam {

    private static final QueryParam EMPTY = new QueryParam(Map.of());
    private static final String ENTRY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> storage;

    private QueryParam(Map<String, String> storage) {
        this.storage = Map.copyOf(storage);
    }

    public static QueryParam from(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return EMPTY;
        }
        final Map<String, String> params = new HashMap<>();
        for (String entry : rawQuery.split(ENTRY_DELIMITER)) {
            if (entry.isBlank()) {
                continue;
            }
            final String[] keyValue = entry.split(KEY_VALUE_DELIMITER, 2);
            final String key = keyValue[0];
            final String value = keyValue.length == 2 ? keyValue[1] : "";
            params.putIfAbsent(key, value);
        }
        return new QueryParam(params);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(storage.get(key));
    }
}
