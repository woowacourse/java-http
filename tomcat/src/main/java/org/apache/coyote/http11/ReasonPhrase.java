package org.apache.coyote.http11;

import java.util.Objects;

public class ReasonPhrase {
    private final String value;

    public ReasonPhrase(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
