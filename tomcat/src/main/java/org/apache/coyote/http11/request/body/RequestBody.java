package org.apache.coyote.http11.request.body;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.*;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    private static final String SEPARATOR = "&";
    private static final String DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> bodies;

    private RequestBody(final Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public static RequestBody from(final String body) {
        if (body == null) {
            return new RequestBody(EMPTY_MAP);
        }
        return Arrays.stream(body.split(SEPARATOR))
                .map(element -> element.split(DELIMITER))
                .collect(collectingAndThen(
                        toMap(element -> element[KEY_INDEX], element -> element[VALUE_INDEX]),
                        RequestBody::new
                ));
    }

    public String getBy(final String key) {
        return bodies.get(key);
    }

    public Map<String, String> bodies() {
        return bodies;
    }

}
