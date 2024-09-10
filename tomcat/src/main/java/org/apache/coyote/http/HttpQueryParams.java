package org.apache.coyote.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpQueryParams {

    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> params;

    public HttpQueryParams(final String queryString) {
        params = new ConcurrentHashMap<>();
        String[] paramSplit = queryString.split(PARAM_SEPARATOR);
        for (String param : paramSplit) {
            String[] keyValue = param.split(KEY_VALUE_SEPARATOR);
            params.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
    }

    public String get(final String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        }
        return null;
    }
}
