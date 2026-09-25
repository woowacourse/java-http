package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String protocol;
    private final String version;

    public RequestLine(final BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        String[] startLine = requestLine.split(" ");

        this.method = HttpMethod.valueOf(startLine[0]);
        this.path = startLine[1];
        this.protocol = startLine[2];
        this.version = startLine[3];
    }

    public HttpMethod getHttpMethod() {
        return method;
    }

    public String getTarget() {
        return path;
    }
}
