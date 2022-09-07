package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryStrings {

    private static final String QUERY_STRING_AND = "&";
    private static final String QUERY_STRING_EQUAL = "=";

    private final Map<String, String> queryStrings;

    public QueryStrings(final String queryStrings) {
        this.queryStrings = parseQueryString(queryStrings);
    }

    private Map<String, String> parseQueryString(final String querystring) {
        final Map<String, String> queryStrings = new LinkedHashMap<>();
        final String[] queries = querystring.split(QUERY_STRING_AND);
        for (String query : queries) {
            final String[] parameterAndValue = query.split(QUERY_STRING_EQUAL);
            queryStrings.put(parameterAndValue[0], parameterAndValue[1]);
        }
        return queryStrings;
    }

    public String getValue(final String parameter) {
        if (!queryStrings.containsKey(parameter)) {
            return "";
        }
        return queryStrings.get(parameter);
    }
}
