package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String PATH_SPLITTER = "?";
    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String PARAMETER_VALUE_SEPARATOR = "=";
    private static final int PARAMETER_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParams = new HashMap<>();

    public QueryParams(String uri) {
        if (uri.contains(PATH_SPLITTER)) {
            int index = uri.indexOf(PATH_SPLITTER);
            parse(uri.substring(index + 1));
        }
    }

    private void parse(String queryString) {
        String[] queries = queryString.split(QUERY_STRING_SEPARATOR);
        for (String query : queries) {
            String[] s = query.split(PARAMETER_VALUE_SEPARATOR);
            queryParams.put(s[PARAMETER_INDEX], s[VALUE_INDEX]);
        }
    }

    public String getParameterValue(String parameter) {
        return queryParams.get(parameter);
    }
}
