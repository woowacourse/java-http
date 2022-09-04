package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Queue;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeader header;

    private HttpRequest(final HttpRequestLine requestLine, final HttpHeader httpHeader) {
        this.requestLine = requestLine;
        this.header = httpHeader;
    }

    public static HttpRequest of(final Queue<String> rawRequest) {
        final HttpRequestLine requestLine = HttpRequestLine.of(rawRequest.remove());
        final HttpHeader httpHeader = HttpHeader.of(rawRequest);

        return new HttpRequest(requestLine, httpHeader);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }
}
