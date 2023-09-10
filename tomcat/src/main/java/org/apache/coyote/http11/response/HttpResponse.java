package org.apache.coyote.http11.response;

import java.util.stream.Collectors;

public class HttpResponse {

    private static final String EMPTY_LINE = "";
    private static final String NEW_LINE = "\r\n";
    private HttpResponseStartLine httpResponseStartLine;
    private HttpResponseHeaders httpResponseHeaders;
    private String responseBody;

    public HttpResponse() {
    }

    public byte[] generateResponse() {
        return String.join(NEW_LINE,
                generateStartLine(),
                generateHeaders(),
                EMPTY_LINE,
                responseBody).getBytes();
    }

    private String generateStartLine() {
        return String.format("%s %s %s",
                httpResponseStartLine.getHttpVersion(),
                httpResponseStartLine.getStatusCode().getCode(),
                httpResponseStartLine.getStatusCode().getText()
        );
    }

    private String generateHeaders() {
        return httpResponseHeaders.getHeaders()
                .stream()
                .map(header -> String.format("%s: %s", header.getKey(), header.getValue()))
                .collect(Collectors.joining(NEW_LINE));
    }

    public void setHttpResponseHeaders(final HttpResponseHeaders httpResponseHeaders) {
        this.httpResponseHeaders = httpResponseHeaders;
    }

    public void setHttpResponseStartLine(final HttpResponseStartLine httpResponseStartLine) {
        this.httpResponseStartLine = httpResponseStartLine;
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }
}
