package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormData {
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private FormData(Map<String, String> values) {
        this.values = values;
    }

    public static FormData from(String body) {
        Map<String, String> values = new LinkedHashMap<>();
        String[] parameters = body.split(PARAMETER_DELIMITER);

        for (String parameter : parameters) {
            String[] keyValue = parameter.split(KEY_VALUE_DELIMITER, 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = "";
            if (keyValue.length > 1) {
                value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
            values.put(key, value);
        }
        return new FormData(values);
    }

    public String get(String name) {
        return values.get(name);
    }
}
