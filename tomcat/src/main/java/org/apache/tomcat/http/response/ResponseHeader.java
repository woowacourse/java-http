package org.apache.tomcat.http.response;

import java.util.stream.Collectors;

import org.apache.tomcat.http.common.Headers;

public class ResponseHeader extends Headers {

    public void put(final String key, final String value) {
        super.values.put(key, value);
    }

    public String get(final String key) {
        return values.getOrDefault(key, "");
    }

    public String getResponseText() {

        return super.values
                .keySet()
                .stream()
                .map(key -> key + KEY_VALUE_DELIMITER + " " + super.getValue(key))
                .collect(Collectors.joining(PARAMETER_DELIMITER));
    }
}
