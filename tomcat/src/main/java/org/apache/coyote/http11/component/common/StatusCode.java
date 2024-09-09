package org.apache.coyote.http11.component.common;

public class StatusCode {

    private final String name;
    private final int value;

    public StatusCode(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getResponseText() {
        return value + " " + name;
    }
}
