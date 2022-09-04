package org.apache.coyote.http11;

public enum Regex {

    BLANK(" "),
    EXTENSION("."),
    QUERY_STRING("?"),
    QUERY_PARAM("&"),
    QUERY_VALUE("="),
    ACCEPT_TYPE(","),
    HEADER_VALUE(": "),
    ;

    private final String value;

    Regex(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
