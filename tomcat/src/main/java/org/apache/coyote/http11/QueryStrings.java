package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {
    private static final String INIT_SIGN = "?";
    private static final String NEXT_SIGN = "&";
    private static final String KEY_VALUE_SIGN = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private QueryStrings(Map<String, String> values) {
        this.values = values;
    }

    public static QueryStrings from(String url) {
        Map<String, String> queryStrings = new HashMap<>();
        int index = url.indexOf(INIT_SIGN);
        if (index != -1) {
            String[] queryString = url.substring(index + 1).split(NEXT_SIGN);
            readQueryStrings(queryString, queryStrings);
        }
        return new QueryStrings(queryStrings);
    }

    private static void readQueryStrings(String[] queryString, Map<String, String> queryStrings) {
        for (String element : queryString) {
            String[] keyAndValue = element.split(KEY_VALUE_SIGN);
            queryStrings.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
    }

    public Map<String, String> getValues() {
        return values;
    }

}
