package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpRequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpRequestBody from(final String requestBody) {
        final Map<String, String> values = new HashMap<>();
        if (!requestBody.isEmpty()) {
            addValue(requestBody.split(REQUEST_BODY_DELIMITER), values);
        }
        return new HttpRequestBody(values);
    }

    private static void addValue(final String[] allRequestBody, final Map<String, String> values) {
        for (String nameAndValue : allRequestBody) {
            final String[] value = nameAndValue.split(VALUE_DELIMITER);
            values.put(value[NAME_INDEX], value[VALUE_INDEX]);
        }
    }

    public String getValue(final String key) {
        return values.get(key);
    }
}
