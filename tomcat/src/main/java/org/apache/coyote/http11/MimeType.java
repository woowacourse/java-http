package org.apache.coyote.http11;

public enum MimeType {

    HTML("text/html"),
    CSS("text/css"),
    ;

    private final String value;

    MimeType(String value) {
        this.value = value;
    }
}
