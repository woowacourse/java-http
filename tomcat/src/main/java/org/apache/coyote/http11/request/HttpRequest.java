package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = new RequestLine(reader.readLine());

        List<String> headers = reader.lines()
                .takeWhile(s -> !s.isBlank())
                .toList();
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        if (requestLine.isPost()) {
            return new HttpRequest(requestLine, requestHeaders, parseRequestBody(reader, requestHeaders.getContentLength()));
        }

        return new HttpRequest(requestLine, requestHeaders, null);
    }

    private static RequestBody parseRequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new RequestBody(new String(buffer));
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isSameUri(final String uri) {
        return this.requestLine.isSamePath(uri);
    }

    public String findBodyValueByKey(final String key) {
        return body.findByKey(key);
    }

    public String getCookie() {
        return getHeader("Cookie"); // TODO: 상수 처리하기
    }

    public String getHeader(final String name) {
        return headers.findHeader(name);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }
}
