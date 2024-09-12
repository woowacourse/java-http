package org.apache.coyote.http11.message.body;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpBody {

    private static final String FORM_DATA_SEPARATOR = "&";
    private static final String FORM_DATA_KEY_AND_VALUE_SEPARATOR = "=";
    private static final int FORM_DATA_KEY_INDEX = 0;
    private static final int FORM_DATA_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpBody(final String httpBodyValue) {
        this.values = parseValues(httpBodyValue);
    }

    private Map<String, String> parseValues(final String httpBodyValue) {
        final Map<String, String> values = new HashMap<>();
        if (httpBodyValue == null || httpBodyValue.isBlank()) {
            return Collections.EMPTY_MAP;
        }

        final String[] splitValues = httpBodyValue.split(FORM_DATA_SEPARATOR);
        Arrays.stream(splitValues)
                .forEach(value -> {
                    final String[] keyAndValue = parseKeyAndValue(value);
                    values.put(keyAndValue[FORM_DATA_KEY_INDEX], keyAndValue[FORM_DATA_VALUE_INDEX]);
                });

        return values;
    }

    private String[] parseKeyAndValue(final String data) {
        final String[] keyAndValue = data.split(FORM_DATA_KEY_AND_VALUE_SEPARATOR);
        if (keyAndValue.length != 2) {
            throw new IllegalArgumentException("유효하지 않은 Form Data 값 입니다. - " + data);
        }

        return keyAndValue;
    }

    public Optional<String> findByKey(final String key) {
        if (!values.containsKey(key)) {
            return Optional.empty();
        }

        return Optional.of(values.get(key));
    }
}
