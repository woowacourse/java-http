package org.apache.coyote.http11.response;

public enum StatusLine {
    OK("200 OK "),
    REDIRECT("302 Found ");

    private static final String PROTOCOL = "HTTP/1.1 ";

    private final String value;

    StatusLine(final String value) {
        this.value = value;
    }

    public String get() {
        return PROTOCOL + value;
    }
}
