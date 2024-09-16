package org.apache.coyote.http11;

public enum Protocol {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String protocol;

    Protocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }
}
