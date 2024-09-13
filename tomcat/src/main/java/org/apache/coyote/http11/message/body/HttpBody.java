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

    private final String value;

    public HttpBody(final String value) {
        this.value = value;
    }

    public Optional<String> getValue() {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(value);
    }

    public Map<String, String> parseFormDataKeyAndValue() {
        final Map<String, String> values = new HashMap<>();
        if (value == null || value.isBlank()) {
            return Collections.EMPTY_MAP;
        }

        final String[] splitValues = value.split(FORM_DATA_SEPARATOR);
        Arrays.stream(splitValues)
                .forEach(value -> setKeyAndValue(values, value));

        return values;
    }

    private void setKeyAndValue(final Map<String, String> values, final String value) {
        final String[] keyAndValue = value.split(FORM_DATA_KEY_AND_VALUE_SEPARATOR);
        if (keyAndValue.length != 2) {
            return;
        }

        values.put(keyAndValue[FORM_DATA_KEY_INDEX], keyAndValue[FORM_DATA_VALUE_INDEX]);
    }

    public String convertResponseMessage() {
        return value;
    }
}
