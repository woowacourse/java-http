package org.apache.coyote.http11.request;

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
        String[] params = input.split("&");
        for (String param : params) {
            if (!param.contains("=")) {
                continue;
            }
            String[] splitParam = param.split("=", -1);
            queryParams.put(splitParam[0], splitParam[1]);
        }

        return new RequestParameters(queryParams);
    }

    public String get(String key) {
        return values.get(key);
    }
}
