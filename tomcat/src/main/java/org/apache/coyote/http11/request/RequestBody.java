package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String FORM_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public RequestBody(final Map<String, String> values) {
        this.values = new HashMap<>();
        this.values.putAll(values);
    }

    public static RequestBody from(final String body) {
        return new RequestBody(Arrays.stream(body.split(FORM_DELIMITER))
                .map(keyValue -> keyValue.split(VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        e -> e[KEY_INDEX],
                        e -> e[VALUE_INDEX]
                )));
    }

    public boolean contains(final String key) {
        return values.containsKey(key);
    }

    public String get(final String key) {
        return values.get(key);
    }
}
