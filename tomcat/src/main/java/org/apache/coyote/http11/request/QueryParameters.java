package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private static final int NOT_EXIST_QUERY_PARAMETER_CHARACTER = -1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

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

    public String getAccount() {
        return values.get(ACCOUNT);
    }

    public String getPassword() {
        return values.get(PASSWORD);
    }

    private static void putKeyValues(String[] eachQueryParameters) {

    }

    private static Map<String, String> processQueryParameters(String uri) {
        Map<String, String> queryParameters = new HashMap<>();
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != NOT_EXIST_QUERY_PARAMETER_CHARACTER) {
            String queryParameter = uri.substring(queryParameterIndex + 1);
            String[] eachQueryParameters = queryParameter.split("&");
            for (String q : eachQueryParameters) {
                String[] keyValues = q.split("=");
                queryParameters.put(keyValues[KEY_INDEX], keyValues[VALUE_INDEX]);
            }
        }
        return queryParameters;
    }
}
