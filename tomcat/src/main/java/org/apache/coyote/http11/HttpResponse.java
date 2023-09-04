package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private String responseBody;

    public HttpResponse() {
        this(null, new HttpHeaders(), null);
    }

    public HttpResponse(final HttpStatusCode statusCode, final HttpHeaders headers, final String responseBody) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public void setHeader(final String key, final String value) {
        headers.add(key, value);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }
}
