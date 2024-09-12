package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SIZE = 2;
    private static final String KEY_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String EMPTY_VALUE = "";

    private final Map<String, String> values;

    public RequestBody(String values) {
        this.values = parseValues(values);
    }

    public Map<String, String> parseValues(String values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(values.split(PARAMETER_DELIMITER))
                .map(cookie -> cookie.trim().split(KEY_DELIMITER, SIZE))
                .collect(Collectors.toMap(
                        result -> result[KEY_INDEX],
                        result -> result[VALUE_INDEX])
                );
    }

    public static RequestBody empty() {
        return new RequestBody(EMPTY_VALUE);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
