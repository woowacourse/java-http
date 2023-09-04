package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.util.HttpHeaderConsts;

public class HttpRequestHeaders {

    private final HttpHeaders headers;

    private HttpRequestHeaders(final HttpHeaders headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders from(final String headerContents) {
        final HttpHeaders headers = HttpHeaders.from(headerContents);

        return new HttpRequestHeaders(headers);
    }

    public boolean isRequestBodyEmpty() {
        final String length = headers.findValue(HttpHeaderConsts.CONTENT_LENGTH);

        return length == null || Integer.parseInt(length) == 0;
    }

    public String findValue(final String headerKey) {
        return headers.findValue(headerKey);
    }


}
