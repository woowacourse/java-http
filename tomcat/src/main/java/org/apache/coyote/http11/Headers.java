package org.apache.coyote.http11;

import java.util.List;

public class Headers {

    private final List<String> headers;

    public Headers(List<String> headers) {
        this.headers = headers;
    }
}
