package org.apache.coyote.http11.response;

public enum Protocol {
    HTTP11("HTTP/1.1");

    private final String name;

    Protocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
