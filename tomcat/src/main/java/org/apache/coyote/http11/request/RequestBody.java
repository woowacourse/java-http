package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String PARAMETER_DELIMITER = "=";

    private final Map<String, String> values;

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody from(String requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            return new RequestBody(new HashMap<>());
        }
        final Map<String, String> values = new HashMap<>();
        final String[] split = requestBody.split(QUERY_STRING_SEPARATOR);

        for (String param : split) {
            String[] splitParam = param.split(PARAMETER_DELIMITER);
            final String key = splitParam[0];
            final String value = splitParam[1];

            values.put(key, value);
        }
        return new RequestBody(values);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
