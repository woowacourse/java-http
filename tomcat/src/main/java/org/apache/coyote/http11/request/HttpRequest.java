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

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean containsQuery() {
        return requestLine.containsQuery();
    }

    public boolean containsQuery(final String key) {
        return requestLine.containsQuery(key);
    }

    public String getQueryParameter(final String queryKey) {
        return requestLine.getQueryParameter(queryKey);
    }
}
