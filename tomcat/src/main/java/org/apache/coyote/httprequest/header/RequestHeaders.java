package org.apache.coyote.httprequest.header;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private final Map<RequestHeaderType, RequestHeader> headers;

    public RequestHeaders() {
        this.headers = new HashMap<>();
    }

    public void putHeader(final RequestHeaderType headerType, final RequestHeader value) {
        headers.put(headerType, value);
    }
}
