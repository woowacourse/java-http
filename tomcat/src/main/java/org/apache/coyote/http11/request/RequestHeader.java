package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class RequestHeader {

    private final Map<String, String> elements;

    public RequestHeader(final Map<String, String> elements) {
        this.elements = elements;
    }

    public String geHeaderValue(final String key) {
        return Optional.ofNullable(elements.get(key))
                .orElse("");
    }

    public Map<String, String> getElements() {
        return elements;
    }
}
