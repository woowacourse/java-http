package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private Map<String, String> values;

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
        final String[] splits = requestBody.split("&");
        for (String split : splits) {
            final String[] value = split.split("=");
            values.put(value[0], value[1]);
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
