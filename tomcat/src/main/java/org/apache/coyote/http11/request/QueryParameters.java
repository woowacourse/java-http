package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameters {

    private static final String PARAMETER_SEPARATOR = "&";
    private static final String ASSIGN_OPERATOR = "=";

    private final Map<String, String> params;

    public QueryParameters(String queryString) {
        if (queryString.isEmpty()) {
            params = new HashMap<>();
            return;
        }

        params = Arrays.stream(queryString.split(PARAMETER_SEPARATOR))
                .filter(param -> param.contains(ASSIGN_OPERATOR))
                .map(param -> param.split(ASSIGN_OPERATOR, 2))
                .collect(Collectors.toMap(param -> param[0], param ->param[1]));
    }

    public static QueryParameters empty() {
        return new QueryParameters("");
    }

    public String get(String name) {
        return params.get(name);
    }
}
