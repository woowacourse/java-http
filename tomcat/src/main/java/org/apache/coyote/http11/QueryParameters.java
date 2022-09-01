package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private static final int NOT_EXIST_QUERY_PARAMETER_CHARACTER = -1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public QueryParameters(String uri) {
        this.values = new HashMap<>();
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != NOT_EXIST_QUERY_PARAMETER_CHARACTER) {
            String queryParameters = uri.substring(queryParameterIndex + 1);
            String[] eachQueryParameters = queryParameters.split("&");
            for (String queryParameter : eachQueryParameters) {
                String[] keyValues = queryParameter.split("=");
                values.put(keyValues[KEY_INDEX], keyValues[VALUE_INDEX]);
            }
        }
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String getAccount() {
        return values.get("account");
    }

    public String getPassword() {
        return values.get("password");
    }
}
