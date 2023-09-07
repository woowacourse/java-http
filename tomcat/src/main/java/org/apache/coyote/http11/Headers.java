package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> values;

    private Headers(Map<String, String> headers) {
        this.values = headers;
    }

    public static Headers from(List<String> headers) {
        Map<String, String> values = new HashMap<>();
        for (String header : headers) {
            String[] keyAndValue = header.split(HEADER_DELIMITER);
            values.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new Headers(values);
    }

    public String getValue(String key) {
        return values.getOrDefault(key, "");
    }

}
