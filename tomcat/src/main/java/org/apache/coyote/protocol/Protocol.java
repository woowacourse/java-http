package org.apache.coyote.protocol;

public enum Protocol {
    HTTP11("HTTP/1.1"),
    ;

    final String value;

    Protocol(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
