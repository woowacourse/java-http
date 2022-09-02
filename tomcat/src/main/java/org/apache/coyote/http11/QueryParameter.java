package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private final Map<String, String> params;

    private QueryParameter(Map<String, String> params) {
        this.params = params;
    }

    public static QueryParameter from(String query) {
        if (query == null) {
            return new QueryParameter(new HashMap<>());
        }

        Map<String, String> params = new HashMap<>();
        final String[] split = query.split("&");

        for (String param : split) {
            String[] splitParam = param.split("=");
            final String key = splitParam[0];
            final String value = splitParam[1];

            params.put(key, value);
        }

        return new QueryParameter(params);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
