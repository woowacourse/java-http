package org.apache.catalina;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpResponseStartLine;
import org.apache.coyote.http11.response.StatusCode;

public class Response {

    private static final String KEY_VALUE_DELIMITER = "=";

    private final HttpResponseHeaders httpResponseHeaders;
    private HttpResponseStartLine httpResponseStartLine;
    private String responseBody;

    public Response() {
        this.httpResponseHeaders = HttpResponseHeaders.empty();
    }

    public void sendRedirect(final String location) {
        httpResponseStartLine = HttpResponseStartLine.of(StatusCode.FOUND);
        httpResponseHeaders.add(HttpHeaders.LOCATION, location);
    }

    public void addCookie(final String key, final String value) {
        httpResponseHeaders.add(HttpHeaders.SET_COOKIE, key + KEY_VALUE_DELIMITER + value);
    }

    public void addHeader(final String name, final String value) {
        httpResponseHeaders.add(name, value);
    }

    public void setHttpResponseStartLine(final StatusCode statusCode) {
        httpResponseStartLine = HttpResponseStartLine.of(statusCode);
    }

    public void setResponseBody(final byte[] responseBody) {
        httpResponseHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBody.length));
        this.responseBody = new String(responseBody);
    }

    public HttpResponseHeaders getHttpResponseHeaders() {
        return httpResponseHeaders;
    }

    public HttpResponseStartLine getHttpResponseStartLine() {
        return httpResponseStartLine;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
