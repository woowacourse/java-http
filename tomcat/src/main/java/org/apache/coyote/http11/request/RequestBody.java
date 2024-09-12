package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {
    private static final String ELEMENT_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY_IDX = 0;
    private static final int VALUE_IDX = 1;

    private String body;

    public RequestBody() {
        this.body = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getUserInformation() {
        return Arrays.stream(body.split(ELEMENT_SEPARATOR))
                .map(element -> Arrays.asList(element.split(KEY_VALUE_SEPARATOR)))
                .filter(keyValue -> keyValue.size() == KEY_VALUE_SIZE)
                .filter(keyValue -> !keyValue.get(KEY_IDX).isBlank() && !keyValue.get(VALUE_IDX).isBlank())
                .collect(Collectors.toMap(
                        keyValue -> keyValue.get(KEY_IDX).trim(),
                        keyValue -> keyValue.get(VALUE_IDX).trim()
                ));
    }
}
