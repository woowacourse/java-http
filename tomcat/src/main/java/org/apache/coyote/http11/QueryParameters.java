package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParameters {
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_SIZE = 2;

    private final Map<String, String> values;

    private QueryParameters(Map<String, String> values) {
        this.values = values;
    }

    public static QueryParameters empty() {
        return new QueryParameters(Map.of());
    }

    public static QueryParameters parse(String encodedParameters) {
        Map<String, String> values = new HashMap<>();

        for (String parameter : encodedParameters.split(PARAMETER_DELIMITER)) {
            if (parameter.isBlank()) {
                continue;
            }
            String[] keyValue = parameter.split(KEY_VALUE_DELIMITER, KEY_VALUE_SIZE);
            if (keyValue.length != KEY_VALUE_SIZE) {
                continue;
            }
            values.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return new QueryParameters(values);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public Optional<String> find(String key) {
        return Optional.ofNullable(values.get(key));
    }
}
