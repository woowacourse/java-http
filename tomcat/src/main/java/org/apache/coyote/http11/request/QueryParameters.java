package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

record QueryParameters(Map<String, String> parameters) {

    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final int KEY_AND_VALUE_COUNT = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static QueryParameters from(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split(PARAMETER_SEPARATOR);
            for (String pair : pairs) {
                String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, KEY_AND_VALUE_COUNT);
                if (keyValue.length == KEY_AND_VALUE_COUNT) {
                    parameters.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
                }
            }
        }

        return new QueryParameters(parameters);
    }

    public String getValueByKey(String key) {
        return parameters.get(key);
    }
}
