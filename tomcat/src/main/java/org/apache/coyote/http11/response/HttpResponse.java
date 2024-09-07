package org.apache.coyote.http11.response;

import java.util.Optional;
import org.apache.coyote.http11.request.HttpHeaders;

public class HttpResponse {
    public static final String PROTOCOL = "HTTP";
    public static final double version = 1.1;

    private final ResponseSerializer serializer;
    private final HttpHeaders headers;
    private final int statusCode;
    private final String statusMessage;
    private final Optional<String> responseBody;

    public HttpResponse(HttpHeaders headers, int statusCode, String statusMessage, Optional<String> responseBody) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = responseBody;
        this.serializer = new ResponseSerializer();
    }

    public HttpResponse(HttpHeaders headers, int statusCode, String statusMessage) {
        this(headers, statusCode, statusMessage, null);
    }

    public String stream() {
        return serializer.serialize(this);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Optional<String> getResponseBody() {
        return responseBody;
    }
}
