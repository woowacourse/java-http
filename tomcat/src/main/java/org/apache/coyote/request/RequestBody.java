package org.apache.coyote.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    public final static RequestBody EMPTY = new RequestBody(Collections.emptyMap());

    private static final String REQUEST_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(final String requestBodyValue) {
        if (requestBodyValue.isBlank()) {
            return EMPTY;
        }

        final Map<String, String> mapping = Arrays.asList(requestBodyValue.split(REQUEST_DELIMITER))
                .stream()
                .map(bodyEntry -> bodyEntry.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(keyValue -> keyValue[NAME_INDEX], keyValue -> keyValue[VALUE_INDEX]));

        return new RequestBody(mapping);
    }

    public List<String> names() {
        return new ArrayList<>(body.keySet());
    }

    public String getBodyValue(final String name) {
        return body.getOrDefault(name, null);
    }

    @Override
    public String toString() {
        return "RequestBody{" +
               "body=" + body +
               '}';
    }
}
