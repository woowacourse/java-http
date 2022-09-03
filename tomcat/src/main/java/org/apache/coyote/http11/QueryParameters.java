package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private final Map<String, String> value;

    private QueryParameters(final Map<String, String> value) {
        this.value = value;
    }

    public static QueryParameters of(final String uri) {
        Map<String, String> queryParams = new HashMap<>();
        int index = uri.indexOf("?");
        if (index != -1) {
            String[] queryString = uri.substring(index + 1).split("&");
            for (String element : queryString) {
                String[] split = element.split("=");
                queryParams.put(split[0], split[1]);
            }
        }
        return new QueryParameters(queryParams);
    }

    public String findValue(String key) {
        return value.get(key);
    }

    public Boolean isEmpty() {
        return value.isEmpty();
    }
}
