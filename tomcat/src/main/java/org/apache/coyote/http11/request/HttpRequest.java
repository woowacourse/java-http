package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.coyote.http11.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody body;

    public HttpRequest(
            final RequestLine requestLine,
            final Headers headers,
            final RequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest form(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestLine requestLine = RequestLine.from(bufferedReader);
        if (requestLine.isNull()) {
            return null;
        }
        final Headers headers = Headers.form(bufferedReader);
        final RequestBody body = RequestBody.of(bufferedReader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }
}
