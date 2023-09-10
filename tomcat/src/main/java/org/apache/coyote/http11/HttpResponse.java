package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    
    private HttpStatusCode statusCode;
    private String protocol;
    private final HttpHeaders headers;
    private String responseBody;

    public HttpResponse() {
        this(null, null, new HttpHeaders(), null);
    }

    public HttpResponse(final HttpStatusCode statusCode, final String protocol, final HttpHeaders headers,
                        final String responseBody) {
        this.statusCode = statusCode;
        this.protocol = protocol;
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
        return headers.getValues();
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public int getContentLength() {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }

    public void setLocation(final String path) {
        headers.add(HttpHeaders.LOCATION, path);
    }

    public void setCookie(final String key, final String value) {
        headers.add(HttpHeaders.SET_COOKIE, String.join(COOKIE_KEY_VALUE_DELIMITER, key, value));
    }
}
