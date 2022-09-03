package org.apache.coyote.http11.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Parameters {

    private final Map<String, String> parameters;

    public Parameters() {
        parameters = new HashMap<>();
    }

    public Parameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static Parameters fromUri(final String uri) {
        final int beginIndex = uri.indexOf("?");
        if (beginIndex < 0) {
            return new Parameters();
        }

        final String queryString = uri.substring(beginIndex + 1);
        return parseQueryString(queryString);
    }

    private static Parameters parseQueryString(final String queryString) {
        final Parameters parameters = new Parameters();
        Arrays.stream(queryString.split("&"))
                .forEach(query -> {
                    final String[] entry = query.split("=");
                    parameters.put(entry[0], entry[1]);
                });

        return parameters;
    }

    public void put(final String key, final String value) {
        parameters.put(key, value);
    }

    public String get(final String key) {
        return parameters.get(key);
    }
}
