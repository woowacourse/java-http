package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private static final String BODY_DELIMITER = "&";
    private static final String BODY_PAIR_DELIMITER = "=";
    private static final int BODY_LIMIT = 2;
    private static final int BODY_KEY_POSITION = 0;
    private static final int BODY_VALUE_POSITION = 1;

    private final Map<String, String> body;

    public RequestBody(String body) {
        this.body = parseRequestBody(body);
    }

    public RequestBody() {
        this("");
    }

    private Map<String, String> parseRequestBody(String body) {
        Map<String, String> bodyParams = new HashMap<>();
        String[] pairs = body.split(BODY_DELIMITER);
        for (String pair : pairs) {
            String[] keyValue = pair.split(BODY_PAIR_DELIMITER);
            if (keyValue.length == BODY_LIMIT) {
                String key = URLDecoder.decode(keyValue[BODY_KEY_POSITION], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[BODY_VALUE_POSITION], StandardCharsets.UTF_8);
                bodyParams.put(key, value);
            }
        }
        return bodyParams;
    }

    public Map<String, String> getBody() {
        return Map.copyOf(body);
    }

    public String getAttribute(String attribute) {
        return body.get(attribute);
    }
}
