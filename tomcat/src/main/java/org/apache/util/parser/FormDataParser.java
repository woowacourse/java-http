package org.apache.util.parser;

import java.util.HashMap;
import java.util.Map;

public class FormDataParser implements Parser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    @Override
    public Map<String, String> parse(String body) {
        String[] bodies = body.split(PARAMETER_DELIMITER);
        Map<String, String> params = new HashMap<>();

        if (body.isEmpty()) {
            return params;
        }

        for (String value : bodies) {
            String[] keyValue = value.split(KEY_VALUE_DELIMITER);
            params.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
        return params;
    }
}
