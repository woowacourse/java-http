package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.header.Headers;

public class HttpRequest {

    private static final String EMPTY_BODY = "";

    private final RequestLine requestLine;
    private final Headers headers;
    private final String body;

    private HttpRequest(
        final RequestLine requestLine,
        final Headers headers,
        final String body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest requestLine(final String requestLine) {
        return new HttpRequest(RequestLine.from(requestLine), Headers.empty(), EMPTY_BODY);
    }

    public boolean containsHeader(final String headerName) {
        return headers.containsHeader(headerName);
    }

    public static Builder builder(final String startLine) {
        return new Builder(startLine);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getRequestUri().getPath();
    }

    public HttpVersion getProtocolVersion() {
        return requestLine.getHttpVersion();
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getRequestUri().getParameters();
    }

    public String getHeader(final String headerName) {
        return headers.getHeaderValue(headerName);
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
            "requestLine=" + requestLine +
            ", headers=" + headers +
            ", body='" + body + '\'' +
            '}';
    }

    public static class Builder {

        private static final String EMPTY_BODY = "";

        private final String requestLine;
        private Headers headers = new Headers(Collections.emptyList());
        private String body = EMPTY_BODY;

        public Builder(final String requestLine) {
            this.requestLine = requestLine;
        }

        public Builder headers(final Headers headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(RequestLine.from(requestLine), headers, body);
        }
    }
}
