package org.apache.coyote.http11.request;

public enum Http11Method {
    GET(false),
    POST(true),
    PUT(true),
    PATCH(true),
    DELETE(true),
    OPTIONS(false),
    HEAD(false),
    CONNECT(false);

    private final boolean hasBody;

    Http11Method(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public boolean hasBody() {
        return this.hasBody;
    }
}
