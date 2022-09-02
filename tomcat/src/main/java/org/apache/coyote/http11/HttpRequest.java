package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequest(final HttpMethod httpMethod, final RequestTarget requestTarget, final String version,
                       final Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.version = version;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestMessage) {
        List<String> requestMessageLines = Arrays.asList(requestMessage.split("\r\n"));

        String requestLine = requestMessageLines.get(0);
        List<String> requestLines = Arrays.asList(requestLine.split(" "));

        String httpVersion = requestLines.get(2);

        Map<String, String> headers = requestMessageLines.subList(1, requestMessageLines.size()).stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        return new HttpRequest(HttpMethod.valueOf(requestLines.get(0)),
                RequestTarget.from(requestLines.get(1)),
                httpVersion,
                headers);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
