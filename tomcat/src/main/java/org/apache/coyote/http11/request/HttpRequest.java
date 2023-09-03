package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String requestUri;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(final HttpMethod httpMethod, final String requestUri, final String httpVersion,
                        final Map<String, String> headers, final String body) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers, final String body) {
        final String[] split = startLine.split(" ");
        return new HttpRequest(
                HttpMethod.from(split[0]),
                split[1],
                split[2],
                headers,
                body
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
