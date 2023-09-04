package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryString {
    private static final String INIT_SIGN = "?";
    private static final String NEXT_SIGN = "&";
    private static final String KEY_VALUE_SIGN = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private QueryString(Map<String, String> values) {
        this.values = values;
    }

    public static QueryString from(String uri) {
        Map<String, String> queryStrings = new HashMap<>();
        int index = uri.indexOf(INIT_SIGN);
        if (index != -1) {
            String[] queryString = uri.substring(index + 1).split(NEXT_SIGN);
            readQueryString(queryString, queryStrings);
        }
        return new QueryString(queryStrings);
    }

    private static void readQueryString(String[] queryString, Map<String, String> queryStrings) {
        for (String element : queryString) {
            String[] keyAndValue = element.split(KEY_VALUE_SIGN);
            queryStrings.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
    }

    public String getValueOf(String key) {
        return values.get(key);
    }

    public Map<String, String> getValues() {
        return values;
    }

}
