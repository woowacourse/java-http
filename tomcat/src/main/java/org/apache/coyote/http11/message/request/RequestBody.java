package org.apache.coyote.http11.message.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String FORM_KEY_VALUE_DELIMITER = "=";
    private static final String FORM_DELIMITER = "&";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> fieldsWithValue;

    private RequestBody(final Map<String, String> fieldsWithValue) {
        this.fieldsWithValue = fieldsWithValue;
    }

    public static RequestBody empty() {
        return new RequestBody(Collections.emptyMap());
    }

    public static RequestBody from(final String body) {
        if (body.isBlank()) {
            return RequestBody.empty();
        }
        final Map<String, String> dataWithValue = mapToDataWithValue(body);
        return new RequestBody(dataWithValue);
    }

    private static Map<String, String> mapToDataWithValue(final String requestBody) {
        final Map<String, String> fieldsWithValue = new HashMap<>();
        final String[] totalData = requestBody.split(FORM_DELIMITER);

        for (final String data : totalData) {
            final String[] keyWithValue = data.split(FORM_KEY_VALUE_DELIMITER);
            fieldsWithValue.put(keyWithValue[KEY_INDEX], keyWithValue[VALUE_INDEX]);
        }
        return fieldsWithValue;
    }

    public String getValueOf(final String field) {
        return fieldsWithValue.get(field);
    }

    public Map<String, String> getFieldsWithValue() {
        return new HashMap<>(fieldsWithValue);
    }
}
