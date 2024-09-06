package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private static final int SPLIT_LIMIT = 2;
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private final Map<String, String> parameters = new HashMap<>();

    public QueryParameter(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }
        Arrays.stream(queryString.split(AMPERSAND))
                .map(param -> param.split(EQUALS, SPLIT_LIMIT))
                .filter(param -> param.length == SPLIT_LIMIT)
                .forEach(param -> parameters.put(param[0], param[1]));
    }

    /**
     * Get the parameter value by name
     * @param name: Parameter name
     * @return Parameter value if exists, or null
     */
    public String get(String name) {
        return parameters.get(name);
    }

    @Override
    public String toString() {
        return parameters.toString();
    }
}
