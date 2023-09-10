package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final HttpRequestLine httpRequestLine = HttpRequestLine.from(requestLine);
        final HttpRequestHeader headers = HttpRequestHeader.from(bufferedReader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(bufferedReader, headers);

        return new HttpRequest(httpRequestLine, headers, httpRequestBody);
    }

    private HttpRequest(
            final HttpRequestLine requestLine,
            final HttpRequestHeader headers,
            final HttpRequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean matchesMethod(final HttpMethod method) {
        return requestLine.getHttpMethod() == method;
    }

    public boolean hasJSessionId() {
        return headers.hasJSessionId();
    }

    public String getJSessionId() {
        return headers.getJSessionId();
    }

    public Uri getUri() {
        return requestLine.getUri();
    }

    public HttpRequestBody getBody() {
        return body;
    }
}
