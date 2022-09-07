package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class HttpBody {

    private static final String FORM_QUERY_DELIMITER = "&";
    private static final String FORM_PARAMS_DELIMITER = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> value;

    private HttpBody(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpBody empty() {
        return new HttpBody(new HashMap<>());
    }

    public static HttpBody formData(final String message) {
        final Map<String, String> bodies = new HashMap<>();

        final String[] bodyElements = message.split(FORM_QUERY_DELIMITER);
        for (final String body : bodyElements) {
            final String[] bodyElement = body.split(FORM_PARAMS_DELIMITER);

            bodies.put(bodyElement[KEY], bodyElement[VALUE]);
        }
        return new HttpBody(bodies);
    }

    public Map<String, String> getValue() {
        return Map.copyOf(value);
    }
}
