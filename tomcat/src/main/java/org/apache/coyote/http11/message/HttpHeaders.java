package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    public static final String FIELD_VALUE_DELIMITER = ": ";
    private static final String VALUES_DELIMITER = ",";
    private static final int FIELD_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> valuesByHeaderField;

    public HttpHeaders(final Map<String, String> valuesByHeaderField) {
        this.valuesByHeaderField = valuesByHeaderField;
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new LinkedHashMap<>());
    }

    public static HttpHeaders from(final List<String> headerLines) {
        final Map<String, String> valuesByHeaderField = new LinkedHashMap<>();
        String[] parsedHeaderLine;

        for (final String line : headerLines) {
            parsedHeaderLine = line.split(FIELD_VALUE_DELIMITER);
            valuesByHeaderField.put(parsedHeaderLine[FIELD_INDEX].trim(), parsedHeaderLine[VALUE_INDEX].trim());
        }
        return new HttpHeaders(valuesByHeaderField);
    }

    public Optional<String> findFirstValueOfField(final String field) {
        return Optional.ofNullable(valuesByHeaderField.get(field))
            .flatMap(values -> Arrays.stream(values.split(VALUES_DELIMITER)).findFirst());
    }

    public Optional<String> getValuesOfField(final String field) {
        return Optional.ofNullable(valuesByHeaderField.get(field));
    }

    public Map<String, String> getValuesByHeaderField() {
        return new LinkedHashMap<>(valuesByHeaderField);
    }

    public void setHeaderWithValue(final String field, final String value) {
        valuesByHeaderField.put(field, value);
    }
}
