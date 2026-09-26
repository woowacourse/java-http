package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String protocol;
    private final String version;

    public RequestLine(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("Request line is empty");
        }

        final String[] startLine = requestLine.trim().split("\\s+");
        if (startLine.length < 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        this.method = HttpMethod.valueOf(startLine[0]);
        this.path = startLine[1];
        this.protocol = startLine[2];
        this.version = startLine[2];
    }

    public HttpMethod getHttpMethod() {
        return method;
    }

    public String getTarget() {
        return path;
    }
}
