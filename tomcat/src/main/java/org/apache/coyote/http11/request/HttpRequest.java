package org.apache.coyote.http11.request;

import java.util.Collections;
import org.apache.coyote.http11.header.Headers;

public class HttpRequest {

    private static final String EMPTY_BODY = "";
    private static final String START_LINE_DELIMITER = " ";
    private static final int START_LINE_PARTS_COUNT = 3;
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;

    private final String startLine;
    private final Headers headers;
    private final String body;

    public HttpRequest(
        final String start,
        final Headers headers,
        final String body
    ) {
        this.startLine = start;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest withStartLine(final String startLine) {
        return new HttpRequest(startLine, Headers.empty(), EMPTY_BODY);
    }

    public String getMethod() {
        final String[] startParts = startLine.split(START_LINE_DELIMITER, START_LINE_PARTS_COUNT);

        return startParts[REQUEST_METHOD_INDEX];
    }

    public String getRequestTarget() {
        final String[] startParts = startLine.split(START_LINE_DELIMITER, START_LINE_PARTS_COUNT);

        return startParts[REQUEST_TARGET_INDEX];
    }

    public boolean containsHeader(final String headerName) {
        return headers.containsHeader(headerName);
    }

    public String getHeaderValueIgnoringCase(final String headerName) {
        return headers.getHeaderValue(headerName);
    }

    public static Builder builder(final String startLine) {
        return new Builder(startLine);
    }

    public String getStartLine() {
        return startLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" + System.lineSeparator() +
            "startLine = '" + startLine + "'," + System.lineSeparator() +
            "header = { " + System.lineSeparator() + headers + System.lineSeparator() +
            "}," + System.lineSeparator() +
            "body='" + body + "'" + System.lineSeparator() +
            "}";
    }

    public static class Builder {

        private final String startLine;
        private Headers headers = new Headers(Collections.emptyList());
        private String body = "";

        public Builder(final String startLine) {
            this.startLine = startLine;
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
            return new HttpRequest(startLine, headers, body);
        }
    }
}
