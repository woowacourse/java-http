package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {

    private final StartLine startLine;
    private final Headers headers;

    public Request(final StartLine startLine, final Headers headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final StartLine requestStartLine = StartLine.from(bufferedReader.readLine());
        final Headers requestHeaders = Headers.from(bufferedReader.lines());

        return new Request(requestStartLine, requestHeaders);
    }

    public String getUri() {
        return startLine.getUri();
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Headers getHeaders() {
        return headers;
    }
}
