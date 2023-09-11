package org.apache.coyote.http11.message.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    public static final QueryParameter EMPTY = new QueryParameter(new HashMap<>());

    private static final String QUERY_PAIR_DELIMITER = "=";
    private static final String QUERY_DELIMITER = "&";
    private static final String QUERY_START_CHARACTER = "?";

    private Map<String, String> parameters = new HashMap<>();

    public QueryParameter(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public QueryParameter(final String uri) {
        final int index = uri.indexOf(QUERY_START_CHARACTER);
        final String queries = uri.substring(index + 1);
        final String[] splitedQueries = queries.split(QUERY_DELIMITER);
        for (final String queryPair : splitedQueries) {
            final int strIndex = queryPair.indexOf(QUERY_PAIR_DELIMITER);
            parameters.put(queryPair.substring(0, strIndex), queryPair.substring(strIndex + 1));
        }
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
