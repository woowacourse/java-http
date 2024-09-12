package org.apache.coyote.http11.component.response;

import java.util.stream.Collectors;

import org.apache.coyote.http11.component.common.Headers;

public class ResponseHeader extends Headers {

    public void put(final String key, final String value) {
        super.values.put(key, value);
    }

    public String getResponseText() {

        return super.values
                .keySet()
                .stream()
                .map(key -> key + KEY_VALUE_DELIMITER + " " + super.getValue(key))
                .collect(Collectors.joining(PARAMETER_DELIMITER));
    }
}
