package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {

    private String uri;
    private Headers headers;

    public Request(final BufferedReader bufferedReader) throws IOException {
        this.uri = extractUri(bufferedReader);
        this.headers = new Headers(bufferedReader);
    }

    private String extractUri(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        return firstLine.split(" ")[1];
    }

    public String getUri() {
        return uri;
    }
}
