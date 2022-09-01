package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestStartLine startLine;

    public HttpRequest(final RequestStartLine startLine) {
        this.startLine = startLine;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new HttpRequest(RequestStartLine.from(bufferedReader.readLine()));
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
