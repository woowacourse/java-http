package org.apache.coyote.http11.response.header;

public abstract class ResponseHeader {

    private final String name;
    private final String value;

    protected ResponseHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public final String getName() {
        return name;
    }

    public final String getValue() {
        return value;
    }
}
