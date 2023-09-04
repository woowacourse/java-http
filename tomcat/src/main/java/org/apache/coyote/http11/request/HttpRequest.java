package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.request.body.HttpBodyLine;
import org.apache.coyote.http11.request.start.HttpMethod;
import org.apache.coyote.http11.request.start.HttpStartLine;
import org.apache.coyote.http11.request.start.HttpVersion;

import java.util.Map;

public class HttpRequest {
    private final HttpStartLine httpStartLine;
    private final HttpHeadersLine httpHeadersLine;
    private final HttpBodyLine httpBodyLine;

    public HttpRequest(
            final HttpStartLine httpStartLine,
            final HttpHeadersLine httpHeadersLine,
            final HttpBodyLine httpBodyLine
    ) {
        this.httpStartLine = httpStartLine;
        this.httpHeadersLine = httpHeadersLine;
        this.httpBodyLine = httpBodyLine;
    }

    public static HttpRequest of(
            final HttpMethod httpMethod,
            final HttpVersion httpVersion,
            final Map<String, String> requestHeader,
            final String resourceName
    ) {
        return new HttpRequest(
                HttpStartLine.from(httpMethod.name() + " /" + resourceName + " " + httpVersion.getVersion()),
                HttpHeadersLine.from(requestHeader),
                HttpBodyLine.empty()
        );
    }

    public static HttpRequest of(
            final HttpStartLine httpStartLine,
            final HttpHeadersLine httpHeadersLine,
            final HttpBodyLine httpBodyLine
    ) {
        return new HttpRequest(httpStartLine, httpHeadersLine, httpBodyLine);
    }

    public HttpStartLine getHttpStartLine() {
        return httpStartLine;
    }

    public HttpHeadersLine getHttpHeadersLine() {
        return httpHeadersLine;
    }

    public Map<String, String> getHttpBodyLine() {
        return httpBodyLine.getBody();
    }
}
