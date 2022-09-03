package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeader headers;
    private final HttpBody body;

    public HttpRequest(final HttpRequestLine requestLine, final HttpHeader headers,
                       final HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) {
        try {
            HttpRequestLine requestLine = HttpRequestLine.of(reader.readLine());
            HttpHeader headers = createHeaders(reader);
            HttpBody body = createBody(reader, headers);
            return new HttpRequest(requestLine, headers, body);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Request에 생성 시, 문제가 발생했습니다.");
        }
    }

    private static HttpHeader createHeaders(final BufferedReader reader) {
        List<String> input = reader.lines()
                .takeWhile(readLine -> !"".equals(readLine))
                .collect(Collectors.toList());
        return HttpHeader.from(input);
    }

    private static HttpBody createBody(final BufferedReader reader, final HttpHeader headers)
            throws IOException {
        int contentLength = headers.getContentLength();
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return HttpBody.from(new String(buffer));
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getParams();
    }

    public String getRequestTarget() {
        return requestLine.getTarget();
    }

    public boolean matchRequestMethod(final Method method) {
        return requestLine.matchMethod(method);
    }

    public Map<String, String> getBody() {
        return this.body
                .getBody();
    }

    public boolean isEmptyQueryParams() {
        return requestLine.hasParams();
    }
}
