package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private Map<String, String> queryMap;

    private QueryParams(final Map<String, String> queryMap) {
        this.queryMap = queryMap;
    }

    public static QueryParams from(String queryString) {
        final Map<String, String> queryMap = new HashMap<>();
        final String[] params = queryString.split("&");

        for (String param : params) {
            final String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                final String key = keyValue[0];
                final String value = keyValue[1];
                queryMap.put(key, value);
            }
        }
        return new QueryParams(queryMap);
    }

    public String getValueFromKey(String key) {
        return queryMap.get(key);
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }
}
