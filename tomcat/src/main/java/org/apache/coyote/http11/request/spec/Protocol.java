package org.apache.coyote.http11.request.spec;

public class Protocol {

    private final String value;

    public Protocol(String value) {
        this.value = value.replace("\r", "").replace("\n", "");
    }

    public String value() {
        return value;
    }
}
