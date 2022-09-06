package org.apache.coyote.http11.httpRequest;

import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String COMPONENT_SEPARATOR = "=";

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public QueryString(Map<String, String> value) {
        this.value = value;
    }

    public static QueryString from(String value) {
        HashMap<String, String> queryString = new HashMap<>();
        if (value.isEmpty()) {
            return new QueryString(queryString);
        }
        String[] strings = value.split(QUERY_STRING_SEPARATOR);
        for (String string : strings) {
            String[] keyValue = string.split(COMPONENT_SEPARATOR);
            queryString.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
        return new QueryString(queryString);
    }

    public String get(String key) {
        return value.get(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }
}
