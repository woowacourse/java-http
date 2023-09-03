package org.apache.coyote.http;

public enum StatusCode {

    OK("200"),
    FOUND("302"),
    ;

    public final String value;

    StatusCode(String value) {
        this.value = value;
    }

    public String renderStatusLine() {
        return "HTTP/1.1 " + value + " " + name() + "\r\n";
    }
}
