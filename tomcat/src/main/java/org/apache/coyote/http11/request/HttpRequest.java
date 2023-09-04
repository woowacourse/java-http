package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;

    private HttpRequest(final RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        final String firstLine = br.readLine();
        final RequestLine requestLine = RequestLine.from(firstLine);

        return new HttpRequest(requestLine);
    }

    public String getUriPath() {
        return requestLine.getPath();
    }

    public String getQueryParameter(final String queryKey) {
        return requestLine.getQueryParameter(queryKey);
    }
}
