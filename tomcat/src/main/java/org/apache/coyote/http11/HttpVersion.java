package org.apache.coyote.http11;

public enum HttpVersion {

    HTTP10("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP20("HTTP/2.0"),
    HTTP30("HTTP/3.0"),
    ;

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }
}
