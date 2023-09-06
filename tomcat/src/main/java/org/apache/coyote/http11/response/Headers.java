package org.apache.coyote.http11.response;

import java.util.List;

public class Headers {

    private final List<Header> headers;

    public Headers(final List<Header> headers) {
        this.headers = headers;
    }

    public List<Header> getHeaders() {
        return headers;
    }
}
