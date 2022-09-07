package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestParameters {

    private final Map<String, String> values;

    private RequestParameters(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestParameters empty() {
        return new RequestParameters(new HashMap<>());
    }

    public static RequestParameters of(final String input) {
        if (input.isBlank()) {
            return new RequestParameters(new HashMap<>());
        }

        Map<String, String> queryParams = new HashMap<>();
        String[] elements = input.split("&");
        for (String element : elements) {
            String[] split = element.split("=");
            queryParams.put(split[0], split[1]);
        }

        return new RequestParameters(queryParams);
    }

    public String get(String key) {
        return values.get(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
