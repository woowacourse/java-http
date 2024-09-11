package org.apache.coyote.http;

import java.io.IOException;
import java.util.StringJoiner;

public class HttpRequest implements HttpComponent {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(final String httpRequest) throws IOException {
        this.requestLine = new HttpRequestLine(httpRequest);
        this.headers = new HttpHeaders(httpRequest);
        String[] parts = httpRequest.split("\r\n\r\n", 2);
        this.body = new HttpBody(parts[1]);
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
    public String asString() {
        final var result = new StringJoiner(LINE_FEED);
        return result.add(requestLine.asString())
                .add(headers.asString())
                .add(body.asString())
                .toString();
    }
}
