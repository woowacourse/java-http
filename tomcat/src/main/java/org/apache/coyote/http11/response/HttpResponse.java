package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpHeaders;

public class HttpResponse {
    private static final String PROTOCOL = "HTTP";
    private static final double version = 1.1;

    private final HttpHeaders headers;
    private final int statusCode;
    private final String statusMessage;
    private final String responseBody;

    public HttpResponse(HttpHeaders headers, int statusCode, String statusMessage, String responseBody) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = responseBody;
    }

    public HttpResponse(HttpHeaders headers, int statusCode, String statusMessage) {
        this(headers, statusCode, statusMessage, null);
    }
}
