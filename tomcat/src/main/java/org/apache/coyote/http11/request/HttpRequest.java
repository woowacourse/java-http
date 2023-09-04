package org.apache.coyote.http11.request;

public class HttpRequest {

    private static final String START_LINE_DELIMITER = " ";
    private static final int START_LINE_PARTS_COUNT = 3;
    private static final int REQUEST_TARGET_INDEX = 1;
    
    private final String startLine;
    private final HttpRequestHeaders headers;
    private final String body;

    public HttpRequest(
        final String start,
        final HttpRequestHeaders headers,
        final String body
    ) {
        this.startLine = start;
        this.headers = headers;
        this.body = body;
    }

    public String getRequestTarget() {
        final String[] startParts = startLine.split(START_LINE_DELIMITER, START_LINE_PARTS_COUNT);

        return startParts[REQUEST_TARGET_INDEX];
    }

    public String getStartLine() {
        return startLine;
    }

    public String getHeaderValueIgnoringCase(final String headerName) {
        return headers.getHeaderValue(headerName);
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
            "startLine='" + startLine + '\'' +
            ", header=" + headers +
            ", body='" + body + '\'' +
            '}';
    }
}
