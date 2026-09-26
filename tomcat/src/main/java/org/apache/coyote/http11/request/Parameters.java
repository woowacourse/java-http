package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

class Parameters {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private Parameters(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    static Parameters empty() {
        return new Parameters(Map.of());
    }

    static Parameters from(String raw) {
        if (raw == null || raw.isEmpty()) {
            return empty();
        }

        Map<String, String> values = new HashMap<>();
        for (String pair : raw.split(PAIR_DELIMITER)) {
            String[] keyValue = pair.split(KEY_VALUE_DELIMITER, 2);
            if (keyValue.length == 2) {
                values.put(keyValue[0], keyValue[1]);
            }
        }
        return new Parameters(values);
    }

    String get(String name) {
        return values.get(name);
    }
}
