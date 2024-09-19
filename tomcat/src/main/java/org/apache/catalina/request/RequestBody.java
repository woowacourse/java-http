package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    public static final String COMPONENT_DELIMITER = "&";
    public static final String COMPONENT_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    public RequestBody() {
        this.body = new HashMap<>();
    }

    public RequestBody(String bodyLine) {
        this.body = mapBody(bodyLine);
    }

    private Map<String, String> mapBody(String bodyLine) {
        if (bodyLine == null || bodyLine.isEmpty()) {
            return Map.of();
        }

        Map<String, String> rawBody = new HashMap<>();
        String[] bodyElements = bodyLine.split(COMPONENT_DELIMITER);
        for (int i = 0; i < bodyElements.length; i++) {
            String[] info = bodyElements[i].split(COMPONENT_VALUE_DELIMITER);
            rawBody.put(info[KEY_INDEX], info[VALUE_INDEX]);
        }
        return rawBody;
    }

    public String get(String key) {
        if (!body.containsKey(key)) {
            throw new IllegalArgumentException("Body parameter not found");
        }
        return body.get(key);
    }
}
