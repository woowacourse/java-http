package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestFirstLine firstLine;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final HttpRequestFirstLine httpRequestFirstLine = HttpRequestFirstLine.from(firstLine);
        final HttpRequestHeader headers = HttpRequestHeader.from(bufferedReader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(bufferedReader, headers);

        return new HttpRequest(httpRequestFirstLine, headers, httpRequestBody);
    }

    private HttpRequest(
            final HttpRequestFirstLine firstLine,
            final HttpRequestHeader headers,
            final HttpRequestBody body
    ) {
        this.firstLine = firstLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean isLogin() {
        final HttpPath httpPath = firstLine.getHttpPath();
        return httpPath.isLogin();
    }

    public boolean isRegister() {
        final HttpPath httpPath = firstLine.getHttpPath();
        return httpPath.isRegister();
    }

    public boolean isCorrectMethod(final HttpMethod method) {
        return firstLine.getHttpMethod() == method;
    }

    public boolean hasJSessionId() {
        return headers.hasJSessionId();
    }

    public String getJSessionId() {
        return headers.getJSessionId();
    }

    public HttpRequestFirstLine getFirstLine() {
        return firstLine;
    }

    public HttpPath getPath() {
        return firstLine.getHttpPath();
    }

    public HttpRequestBody getBody() {
        return body;
    }
}
