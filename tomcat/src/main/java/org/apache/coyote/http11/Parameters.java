package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Parameters {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private Parameters(final Map<String, String> values) {
        this.values = values;
    }

    public static Parameters empty() {
        return new Parameters(Map.of());
    }

    public static Parameters from(final String rawParameters) {
        final Map<String, String> values = new HashMap<>();
        for (String pair : rawParameters.split(PAIR_DELIMITER)) {
            final String[] keyValue = pair.split(KEY_VALUE_DELIMITER, 2);
            if (keyValue.length == 2) {
                values.put(decode(keyValue[0]), decode(keyValue[1]));
            }
        }
        return new Parameters(values);
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public String get(final String name) {
        return values.get(name);
    }
}
