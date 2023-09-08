package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Body {

    public static final Body EMPTY_BODY = new Body(new HashMap<>());
    private static final String FORM_DATA_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> values;

    private Body(final Map<String, String> values) {
        this.values = values;
    }

    public static Body from(final String request) {
        final Map<String, String> values = Arrays.stream(request.split(FORM_DATA_SEPARATOR))
                                                 .map(value -> value.split(KEY_VALUE_SEPARATOR))
                                                 .collect(toMap(value -> value[0], value -> value[1]));

        return new Body(values);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
