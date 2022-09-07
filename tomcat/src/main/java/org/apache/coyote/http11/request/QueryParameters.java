package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private QueryParameters(Map<String, String> uri) {
        this.values = uri;
    }

    public static QueryParameters of(String uri) {
        Map<String, String> queryParameters = processQueryParameters(uri);
        return new QueryParameters(queryParameters);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    private static Map<String, String> processQueryParameters(String uri) {
        Map<String, String> queryParameters = new HashMap<>();

        if (!uri.contains("?")) {
            return queryParameters;
        }
        int queryParameterIndex = uri.indexOf("?");
        String queryParameter = uri.substring(queryParameterIndex + 1);
        putKeyValues(queryParameters, queryParameter);
        return queryParameters;
    }

    private static void putKeyValues(Map<String, String> queryParameters, String queryParameter) {
        String[] eachQueryParameters = queryParameter.split("&");
        for (String q : eachQueryParameters) {
            String[] keyValues = q.split("=");
            if (keyValues.length == 1) {
                queryParameters.put(keyValues[KEY_INDEX], null);
                continue;
            }
            queryParameters.put(keyValues[KEY_INDEX], keyValues[VALUE_INDEX]);
        }
    }

    public String get(String name) {
        return values.get(name);
    }
}
