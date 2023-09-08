package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.request.body.HttpBodyLine;
import org.apache.coyote.http11.request.start.HttpExtension;
import org.apache.coyote.http11.request.start.HttpMethod;
import org.apache.coyote.http11.request.start.HttpStartLine;

import java.util.Arrays;
import java.util.Optional;

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

    public Optional<String> getCookie() {
        final String cookies = httpHeadersLine.getHeaders().get("Cookie");
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies.split(";"))
                .filter(cookie -> cookie.contains("JSESSIONID"))
                .map(cookie -> Optional.of(cookie.split("=")[1]))
                .findAny()
                .orElseGet(Optional::empty);
    }

    public HttpStartLine getHttpStartLine() {
        return httpStartLine;
    }

    public HttpHeadersLine getHttpHeadersLine() {
        return httpHeadersLine;
    }

    public String getBodyBy(final String key) {
        return httpBodyLine.getBody().get(key);
    }

    public String getPath() {
        return httpStartLine.getRequestTarget().getPath();
    }

    public HttpExtension getHttpExtension() {
        return httpStartLine.getRequestTarget().getExtension();
    }

    public HttpMethod getHttpMethod() {
        return httpStartLine.getHttpMethod();
    }
}
