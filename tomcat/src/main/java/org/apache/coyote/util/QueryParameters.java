package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private final Map<String, String> queryParameters;

    private QueryParameters(final Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public static QueryParameters parseFrom(final String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        String[] parameterPairs = queryString.split("&");
        for (String parameterPair : parameterPairs) {
            String[] keyValue = parameterPair.split("=");
            queryParameters.put(keyValue[0], keyValue[1]);
        }
        return new QueryParameters(queryParameters);
    }

    public static QueryParameters createEmpty() {
        return new QueryParameters(new HashMap<>());
    }

    public String getValue(final String parameterKey) {
        return queryParameters.get(parameterKey);
    }
}
