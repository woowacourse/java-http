package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private static final String PARAMETER_SEPARATOR = "&";
    private static final String ASSIGN_OPERATOR = "=";

    private final Map<String, String> params;

    public QueryParameters(String queryString) {
        params = new HashMap<>();
        if (queryString.isEmpty()) {
            return;
        }
        for (String param : queryString.split(PARAMETER_SEPARATOR)) {
            int index = param.indexOf(ASSIGN_OPERATOR);
            this.params.put(param.substring(0, index), param.substring(index + 1));
        }
    }
}
