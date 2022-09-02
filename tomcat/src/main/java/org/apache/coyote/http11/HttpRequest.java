package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private String uri;
    private HttpHeaders httpHeaders;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.uri = extractUri(bufferedReader);
        this.httpHeaders = new HttpHeaders(bufferedReader);
    }

    private String extractUri(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        return firstLine.split(" ")[1];
    }

    public String getUri() {
        return uri;
    }
}
