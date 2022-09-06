package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FormData {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public FormData(Map<String, String> values) {
        this.values = values;
    }

    public static FormData from(String formDataLine) {
        formDataLine = formDataLine.trim();
        String[] queryParameters = formDataLine.split(QUERY_STRING_DELIMITER);
        Map<String, String> params = initializeData(queryParameters);
        return new FormData(params);
    }

    private static Map<String, String> initializeData(String[] queryParameters) {
        Map<String, String> params = new ConcurrentHashMap<>();
        for (String queryParameter : queryParameters) {
            String[] keyAndValue = queryParameter.split(KEY_VALUE_DELIMITER);
            params.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return params;
    }

    public Map<String, String> getValues() {
        return values;
    }
}
