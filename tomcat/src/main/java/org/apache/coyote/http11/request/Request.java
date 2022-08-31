package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Request {

    private final RequestStartLine startLine;

    public Request(final RequestStartLine startLine) {
        this.startLine = startLine;
    }

    public static Request from(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new Request(RequestStartLine.from(bufferedReader.readLine()));
    }

    public RequestStartLine getStartLine() {
        return startLine;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }
}
