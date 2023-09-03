package org.apache.coyote.http11.response;

import java.io.IOException;

public class HttpResponse {

    private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";
    private static final HttpResponseStatusLine DEFAULT_RESPONSE_STATUS =
            HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, HttpResponseStatus.OK);

    private final HttpResponseHeaders httpResponseHeaders = HttpResponseHeaders.getInstance();
    private HttpResponseStatusLine httpResponseStatusLine = DEFAULT_RESPONSE_STATUS;
    private String body;

    public HttpResponseStatusLine getHttpResponseStatusLine() {
        return httpResponseStatusLine;
    }

    public HttpResponseHeaders getHttpResponseHeaders() {
        return httpResponseHeaders;
    }

    public String getBody() {
        return body;
    }

    public void updateHttpResponseStatusLineByStatus(final HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatusLine = HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, httpResponseStatus);
    }

    public void setBody(final String body) {
        httpResponseHeaders.add("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void setHeader(final String header, final String value) {
        httpResponseHeaders.add(header, value);
    }

    public void setCookie(final String cookieKey, final String cookieValue) {
        httpResponseHeaders.add("Set-Cookie", cookieKey + "=" + cookieValue);
    }

    public void updateByResponseEntity(final ResponseEntity response) throws IOException {
        updateHttpResponseStatusLineByStatus(response.getHttpResponseStatus());
        setHeader("Content-Type", response.getView().getContentType());
        response.getHeaders().forEach(this::setHeader);

        setBody(response.getView().renderView());
    }
}
