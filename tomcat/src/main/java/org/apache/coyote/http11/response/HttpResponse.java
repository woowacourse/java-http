package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {

    private final String httpVersion;
    private final StatusCode statusCode;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(final String httpVersion, final StatusCode statusCode, final Map<String, String> headers,
                        final String body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(final String httpVersion, final StatusCode statusCode, final Map<String, String> headers) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = "";
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
