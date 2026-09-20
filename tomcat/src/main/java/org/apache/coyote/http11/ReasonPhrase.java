package org.apache.coyote.http11;

public class ReasonPhrase {
    private final String value;

    public ReasonPhrase(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
