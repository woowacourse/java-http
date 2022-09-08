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
            return new Parameters(new HashMap<>());
        }

        final String queryString = uri.substring(beginIndex + 1);
        return parseParameters(queryString, "&");
    }

    public static Parameters parseParameters(final String values, final String delimiter) {
        final Map<String, String> parameters = new HashMap<>();
        Arrays.stream(values.split(delimiter))
                .forEach(query -> {
                    final String[] entry = query.trim().split("=");
                    parameters.put(entry[0].trim(), entry[1].trim());
                });

        return new Parameters(parameters);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String get(final String key) {
        return parameters.get(key);
    }
}
