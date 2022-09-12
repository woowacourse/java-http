package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpDataRequest {
    private static final String REQUEST_STANDARD = "&";
    private static final String DATA_STANDARD = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private HttpDataRequest(Map<String, String> values) {
        this.values = values;
    }

    public static HttpDataRequest extractRequest(String query) {
        String[] dataMap = query.split(REQUEST_STANDARD);
        Map<String, String> mp = new HashMap<>();
        for (String data : dataMap) {
            mp.put(getKey(data), getValue(data));
        }
        return new HttpDataRequest(mp);
    }

    private static String getKey(String dataMap) {
        return dataMap.split(DATA_STANDARD)[KEY_INDEX];
    }

    private static String getValue(String dataMap) {
        return dataMap.split(DATA_STANDARD)[VALUE_INDEX];
    }

    public String get(String key) {
        return values.get(key);
    }
}
