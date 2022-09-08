package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final int REQUEST_BODY_KEY_INDEX = 0;
    private static final int REQUEST_BODY_VALUE_INDEX = 1;
    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String REQUEST_BODY_VALUE_DELEMITER = "=";

    private final Map<String, String> values;

    private RequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody from(final String requestBody) {
        return new RequestBody(convertRequestBody(requestBody));
    }

    private static Map<String, String> convertRequestBody(final String requestBody) {
        Map<String, String> values = new HashMap<>();
        if (requestBody.isEmpty()) {
            return values;
        }
        final String[] splits = requestBody.split(REQUEST_BODY_DELIMITER);
        for (String split : splits) {
            final String[] value = split.split(REQUEST_BODY_VALUE_DELEMITER);
            values.put(value[REQUEST_BODY_KEY_INDEX], value[REQUEST_BODY_VALUE_INDEX]);
        }
        return values;
    }

    public static RequestBody empty() {
        return new RequestBody(new HashMap<>());
    }

    public Map<String, String> getValues() {
        return values;
    }
}
