package org.apache.coyote.http11.component;

import java.util.stream.Collectors;

public class ResponseHeader extends Headers {

    public ResponseHeader() {
    }

    public void put(final String key, final String value) {
        super.values.put(key, value);
    }

    public String getResponseText() {

        return super.values
                .keySet()
                .stream()
                .map(key -> key + KEY_VALUE_DELIMITER + super.getValue(key))
                .collect(Collectors.joining(PARAMETER_DELIMITER));
    }
}
