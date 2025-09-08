package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class FormDataParser {
    
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    
    public static Map<String, String> parse(String formData) {
        if (formData == null || formData.isBlank()) {
            throw new IllegalArgumentException("폼 데이터가 비어있습니다.");
        }
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split(PARAM_SEPARATOR);
        for (String pair : pairs) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0].strip(), keyValue[1].strip());
            }
        }
        return params;
    }
}
