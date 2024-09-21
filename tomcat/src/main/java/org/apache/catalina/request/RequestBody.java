package org.apache.catalina.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

        return Arrays.stream(bodyLine.split(COMPONENT_DELIMITER))
                .map(bodyElement -> bodyElement.split(COMPONENT_VALUE_DELIMITER))
                .filter(bodyElements -> bodyElements.length == 2)
                .collect(Collectors.toMap(bodyElements -> bodyElements[KEY_INDEX],
                        bodyElements -> bodyElements[VALUE_INDEX]));
    }

    public String get(String key) {
        if (!body.containsKey(key)) {
            throw new IllegalArgumentException("Body parameter not found");
        }
        return body.get(key);
    }
}
