package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class HttpRequest {

    static final String LINE_FEED = "\r\n";

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final var reader = new InputStreamReader(inputStream);
        final var buffer = new BufferedReader(reader);
        this.requestLine = new HttpRequestLine(buffer);
        this.headers = new HttpHeaders(buffer);
        this.body = new HttpBody(buffer);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpQueryParams getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getVersion();
    }

    public String getAccept() {
        return headers.get(HttpHeaders.ACCEPT);
    }

    public HttpBody getBody() {
        return body;
    }

    @Override
    public String toString() {
        final var result = new StringJoiner(LINE_FEED);
        return result.add(requestLine.asString())
                .add(headers.asString())
                .add(body.asString())
                .toString();
    }
}
