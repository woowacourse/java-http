package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class RequestBody {

    public static final RequestBody EMPTY_REQUEST_BODY = new RequestBody(new HashMap<>());
    private static final String FORM_DATA_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private RequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody from(final String request) {
        final Map<String, String> values = Arrays.stream(request.split(FORM_DATA_SEPARATOR))
                                                 .map(value -> value.split(KEY_VALUE_SEPARATOR))
                                                 .filter(value -> value.length > VALUE_INDEX)
                                                 .collect(toMap(value -> value[KEY_INDEX], value -> value[VALUE_INDEX]));

        return new RequestBody(values);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
