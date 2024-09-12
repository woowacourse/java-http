package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int MAX_KEY_VALUE_PAIRS = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body = new HashMap<>();

    public HttpRequestBody(final String bodyLine) {
        Arrays.stream(bodyLine.split(PARAMETER_DELIMITER))
                .map(parameter -> parameter.split(KEY_VALUE_DELIMITER, MAX_KEY_VALUE_PAIRS))
                .filter(parts -> parts.length == MAX_KEY_VALUE_PAIRS)
                .forEach(parts -> body.put(parts[KEY_INDEX].trim(), parts[VALUE_INDEX].trim()));
    }

    public String get(final String key) {
        return body.get(key);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
