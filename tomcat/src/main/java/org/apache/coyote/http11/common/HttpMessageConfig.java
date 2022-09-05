package org.apache.coyote.http11.common;

public enum HttpMessageConfig {

    WORD_DELIMITER(" "),
    LINE_DELIMITER("\r\n"),
    HEADER_BODY_DELIMITER(""),
    ;

    private final String value;

    HttpMessageConfig(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
