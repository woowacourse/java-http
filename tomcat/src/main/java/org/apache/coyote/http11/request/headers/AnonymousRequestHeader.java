package org.apache.coyote.http11.request.headers;

public class AnonymousRequestHeader implements RequestHeader {

    private final String field;
    private final String value;

    public AnonymousRequestHeader(String field, String value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getValue() {
        return value;
    }
}
