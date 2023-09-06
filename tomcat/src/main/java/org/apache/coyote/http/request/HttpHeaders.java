package org.apache.coyote.http.request;

import java.util.Map;

public class HttpHeaders {

    private final Map<HttpHeader, String> headers;

    public HttpHeaders(final Map<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public String get(final HttpHeader headerName) {
        if (!headers.containsKey(headerName)) {
            throw new IllegalStateException("HttpRquest 에 존재하지 않는 HttpHeader 입니다.");
        }

        return headers.get(headerName);
    }

    public boolean containsKey(final HttpHeader headerName) {
        return headers.containsKey(headerName);
    }
}
