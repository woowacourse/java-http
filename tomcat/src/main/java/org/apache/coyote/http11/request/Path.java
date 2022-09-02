package org.apache.coyote.http11.request;

public class Path {

    private final String value;

    public Path(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
