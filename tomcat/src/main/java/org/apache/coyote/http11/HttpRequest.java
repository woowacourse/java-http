package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion httpVersion;
    private final Map<String, String> headers;

    private HttpRequest(final HttpMethod httpMethod, final RequestTarget requestTarget, final HttpVersion httpVersion,
                        final Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestMessage) {
        List<String> requestMessageLines = Arrays.asList(requestMessage.split("\r\n"));
        String requestLine = requestMessageLines.get(0);
        Map<String, String> headers = requestMessageLines.subList(1, requestMessageLines.size()).stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        List<String> requestLines = Arrays.asList(requestLine.split(" "));
        return new HttpRequest(HttpMethod.valueOf(requestLines.get(0)),
                RequestTarget.from(requestLines.get(1)),
                HttpVersion.valueOf(requestLines.get(2)),
                headers);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
