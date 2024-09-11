package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Http11RequestBody {

    private static final String ATTRIBUTE_DELIMITER = "&";
    private static final String ATTRIBUTE_KEY_VALUE_DELIMITER = "=";
    private static final int ATTRIBUTE_KEY_INDEX = 0;
    private static final int ATTRIBUTE_VALUE_INDEX = 1;

    private final String value;

    public Http11RequestBody(String requestBody) {
        this.value = requestBody;
    }

    public static Http11RequestBody ofEmpty() {
        return new Http11RequestBody("");
    }

    public Map<String, String> parseBody() {
        if (!exists()) {
            throw new UnsupportedOperationException("Body 가 존재하지 않는 요청입니다.");
        }
        Map<String, String> param = new HashMap<>();
        for (String query : value.split(ATTRIBUTE_DELIMITER)) {
            String key = query.split(ATTRIBUTE_KEY_VALUE_DELIMITER)[ATTRIBUTE_KEY_INDEX];
            String value = query.split(ATTRIBUTE_KEY_VALUE_DELIMITER)[ATTRIBUTE_VALUE_INDEX];
            param.put(key, value);
        }
        return param;
    }

    private boolean exists() {
        return value.length() > 0;
    }
}
