package org.apache.coyote.http11;

public enum HttpMethod {
    GET(false),
    POST(true),
    PUT(true),
    PATCH(true),
    DELETE(true),
    OPTIONS(false),
    HEAD(false),
    CONNECT(false);

    private final boolean hasBody;

    HttpMethod(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public boolean hasBody() {
        return this.hasBody;
    }
}
