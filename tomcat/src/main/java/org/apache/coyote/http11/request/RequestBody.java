package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String DELIMITER = "&";
    private static final String REGEX = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SPLIT_LIMIT = 2;

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody empty() {
        return new RequestBody(new HashMap<>());
    }

    public static RequestBody parse(final String bodyLine) {
        if (bodyLine == null) {
            return new RequestBody(new HashMap<>());
        }
        return Arrays.stream(bodyLine.split(DELIMITER))
                .map(field -> field.split(REGEX, SPLIT_LIMIT))
                .collect(collectingAndThen(
                        toMap(field -> field[KEY_INDEX], field -> field[VALUE_INDEX]),
                        RequestBody::new
                ));
    }

    public Map<String, String> getBody() {
        return body;
    }
}
