package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class RequestHeader {

    private static final String EMPTY_STRING = "";

    private final Map<String, String> elements;

    public RequestHeader(final Map<String, String> elements) {
        this.elements = elements;
    }

    public String geHeaderValue(final String key) {
        return Optional.ofNullable(elements.get(key))
                .orElse(EMPTY_STRING);
    }

    public Map<String, String> getElements() {
        return elements;
    }
}
