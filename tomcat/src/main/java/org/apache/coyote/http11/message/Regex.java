package org.apache.coyote.http11.message;

public enum Regex {

    BLANK(" "),
    EXTENSION("."),
    QUERY_STRING("?"),
    QUERY_PARAM("&"),
    KEY_VALUE("="),
    ACCEPT_TYPE(","),
    HEADER_VALUE(": "),
    COOKIE_VALUE("; "),
    ;

    private final String value;

    Regex(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
