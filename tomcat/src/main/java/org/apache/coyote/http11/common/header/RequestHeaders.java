package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderName.COOKIE;

import java.util.Map;

public class RequestHeaders extends Headers {

    public RequestHeaders() {
    }

    public RequestHeaders(final Map<String, String> headers) {
        super(headers);
    }

    @Override
    public boolean isType(final String headerName) {
        return HeaderName.isRequestHeaders(headerName);
    }

    public String getCookie() {
        return find(COOKIE);
    }

}
