package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.mvc.view.ResponseEntity;

public class HttpResponse {

    private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";
    private static final HttpResponseStatusLine DEFAULT_RESPONSE_STATUS =
            HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, HttpStatus.OK);

    private final HttpHeaders httpHeaders = HttpHeaders.getInstance();
    private HttpResponseStatusLine httpResponseStatusLine = DEFAULT_RESPONSE_STATUS;
    private String body;

    public void updateHttpResponseStatusLineByStatus(final HttpStatus httpStatus) {
        this.httpResponseStatusLine = HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, httpStatus);
    }

    public void updateByResponseEntity(final ResponseEntity response) throws IOException {
        updateHttpResponseStatusLineByStatus(response.getHttpStatus());
        setHeader("Content-Type", response.getView().getContentType());
        response.getHeaders().forEach(this::setHeader);
        setBody(response.getView().renderView());
    }

    public HttpResponseStatusLine getHttpResponseStatusLine() {
        return httpResponseStatusLine;
    }

    public HttpHeaders getHttpResponseHeaders() {
        return httpHeaders;
    }

    public String getBody() {
        return body;
    }
    
    public void setBody(final String body) {
        httpHeaders.setHeader("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void setHeader(final String header, final String value) {
        httpHeaders.setHeader(header, value);
    }

    public void setCookie(final String cookieKey, final String cookieValue) {
        httpHeaders.setHeader("Set-Cookie", cookieKey + "=" + cookieValue);
    }
}
