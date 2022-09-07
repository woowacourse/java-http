package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParameters;

    public QueryParameter(final Map<String, String> queryParams) {
        this.queryParameters = queryParams;
    }

    public static QueryParameter of(final String queryString) {
        final Map<String, String> queryParams = new HashMap<>();
        if (!queryString.equals("")) {
            parseQueryString(queryParams, queryString);
        }

        return new QueryParameter(queryParams);
    }

    private static void parseQueryString(final Map<String, String> queryParams, final String queryString) {
        final String[] queries = queryString.split("&");
        for (final String queryParam : queries) {
            final String[] parsedQuery = queryParam.split("=");
            queryParams.put(parsedQuery[NAME_INDEX], parsedQuery[VALUE_INDEX]);
        }
    }

    public boolean contains(final String parameterName) {
        return queryParameters.containsKey(parameterName);
    }

    public String getParameter(final String parameterName) {
        return queryParameters.get(parameterName);
    }
}
