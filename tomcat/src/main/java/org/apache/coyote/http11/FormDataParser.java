package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
                String key = URLDecoder.decode(keyValue[0].strip(), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1].strip(), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }
}
