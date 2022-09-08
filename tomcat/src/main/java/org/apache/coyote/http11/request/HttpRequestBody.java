package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestBody {

    private static final String FORM_QUERY_DELIMITER = "&";
    private static final String FORM_PARAMS_DELIMITER = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> value;

    private HttpRequestBody(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(new ConcurrentHashMap<>());
    }

    public static HttpRequestBody formData(final String message) {
        final Map<String, String> bodies = new HashMap<>();

        final String[] bodyElements = message.split(FORM_QUERY_DELIMITER);
        for (final String body : bodyElements) {
            final String[] bodyElement = body.split(FORM_PARAMS_DELIMITER);

            bodies.put(bodyElement[KEY], readValue(bodyElement));
        }
        return new HttpRequestBody(bodies);
    }

    private static String readValue(final String[] bodyElement) {
        if (bodyElement.length < 2) {
            return "";
        }
        return bodyElement[VALUE];
    }

    public Map<String, String> getValue() {
        return Map.copyOf(value);
    }
}
