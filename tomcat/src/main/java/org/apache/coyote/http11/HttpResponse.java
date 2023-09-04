package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private final String responseBody;

    public HttpResponse(final HttpStatusCode statusCode, final HttpHeaders headers, final String responseBody) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public void setHeader(final String key, final String value) {
        headers.add(key, value);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getResponseBody() {
        return responseBody;
    }
}
