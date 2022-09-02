package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryProcessor {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_PARAMETER_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParameters;

    private QueryProcessor(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public static QueryProcessor from(String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();
        final String[] queries = queryString.split(QUERY_STRING_DELIMITER);

        for (String query : queries) {
            final String[] values = query.split(QUERY_PARAMETER_DELIMITER);
            queryParameters.put(values[KEY_INDEX], values[VALUE_INDEX]);
        }

        return new QueryProcessor(queryParameters);
    }

    public String getParameter(final String parameter) {
        return queryParameters.get(parameter);
    }
}
