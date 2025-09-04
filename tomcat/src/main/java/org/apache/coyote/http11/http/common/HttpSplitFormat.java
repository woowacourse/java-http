package org.apache.coyote.http11.http.common;

public enum HttpSplitFormat {

    START_LINE(" "),
    HEADER(":"),
    QUERY_PARAMETER_START("?"),
    QUERY_PARAMETER("&"),
    QUERY_PARAMETER_ELEMENT("="),
    ;

    private final String value;

    HttpSplitFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
