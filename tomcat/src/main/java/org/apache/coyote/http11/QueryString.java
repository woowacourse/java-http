package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryString {

    private final Map<String, String> value;

    public QueryString(Map<String, String> value) {
        this.value = value;
    }

    public static QueryString from(String value) {
        HashMap<String, String> queryString = new HashMap<>();
        if (value.isEmpty()) {
            return new QueryString(queryString);
        }
        String[] strings = value.split("&");
        for (String string : strings) {
            String[] keyValue = string.split("=");
            queryString.put(keyValue[0], keyValue[1]);
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
