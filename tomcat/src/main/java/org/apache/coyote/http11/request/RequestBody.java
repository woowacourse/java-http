package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    public RequestBody(String body) {
        this.body = Arrays.stream(body.split(QUERY_PARAMETER_SEPARATOR))
                .map(pair -> pair.split(KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(parts -> parts[KEY_INDEX], parts -> parts[VALUE_INDEX]));
    }

    public String findByKey(final String key) {
        return body.get(key);
    }
}
