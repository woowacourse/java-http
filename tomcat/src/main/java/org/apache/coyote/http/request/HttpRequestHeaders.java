package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpHeaders;

public class HttpRequestHeaders {

    private final HttpHeaders headers;

    private HttpRequestHeaders(final HttpHeaders headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders from(final String headerContents) {
        final HttpHeaders headers = HttpHeaders.from(headerContents);

        return new HttpRequestHeaders(headers);
    }

    public String findValue(final String headerKey) {
        return headers.findValue(headerKey);
    }
}
