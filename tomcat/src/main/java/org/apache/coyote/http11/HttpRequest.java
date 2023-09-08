package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final HttpRequestLine httpRequestLine = HttpRequestLine.from(firstLine);
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

    public boolean isLogin() {
        final Uri uri = requestLine.getUri();
        return uri.isLogin();
    }

    public boolean isRegister() {
        final Uri uri = requestLine.getUri();
        return uri.isRegister();
    }

    public boolean isCorrectMethod(final HttpMethod method) {
        return requestLine.getHttpMethod() == method;
    }

    public boolean hasJSessionId() {
        return headers.hasJSessionId();
    }

    public String getJSessionId() {
        return headers.getJSessionId();
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public Uri getPath() {
        return requestLine.getUri();
    }

    public HttpRequestBody getBody() {
        return body;
    }
}
