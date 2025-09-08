package org.apache.coyote.http11.httpRequest;

import java.util.HashMap;
import java.util.Map;

public class Params {

    private final Map<String, String> params;

    private Params(
            final Map<String, String> params
    ) {
        this.params = params;
    }

    public static Params parse(final String queryString) {
        if(queryString == null || queryString.isBlank()) {
            return new Params(Map.of());
        }

        final String[] queryStrings = queryString.split("&");
        final Map<String, String> params = new HashMap<>();

        for (String param : queryStrings) {
            final String name = param.split("=")[0];
            final String value = param.split("=")[1];
            params.put(name, value);
        }

        return new Params(params);
    }

    public static Params empty() {
        return new Params(Map.of());
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}
