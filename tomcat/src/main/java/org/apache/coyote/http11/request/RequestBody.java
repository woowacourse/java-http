package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String REQUEST_BODY_PARAMETER_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> bodies;

    private RequestBody(Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public static RequestBody ofEmpty() {
        return new RequestBody(Map.of());
    }

    public static RequestBody of(String value) {
        return new RequestBody(parseToMap(value));
    }

    private static Map<String, String> parseToMap(String requestBodyValue) {
        return Arrays.stream(requestBodyValue.split(REQUEST_BODY_DELIMITER))
                .collect(Collectors.toMap(value -> value.split(REQUEST_BODY_PARAMETER_DELIMITER)[KEY_INDEX],
                        value -> value.split(REQUEST_BODY_PARAMETER_DELIMITER)[VALUE_INDEX]));
    }

    public String getValue(String field) {
        return bodies.get(field);
    }
}
