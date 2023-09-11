package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.view.SimpleStringDataView;
import org.apache.coyote.http11.view.StaticResourceView;
import org.apache.coyote.http11.view.View;

public class HttpResponse {

    private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";
    private static final HttpResponseStatusLine DEFAULT_RESPONSE_STATUS =
            HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, HttpStatus.OK);
    private static final String NO_CONTENT = "";
    private static final String DEFAULT_METHOD_NOT_ALLOWED_RESOURCE = "/405.html";
    private static final String DEFAULT_NOT_FOUND_RESOURCE = "/404.html";

    private final HttpHeaders httpHeaders = HttpHeaders.getInstance();
    private HttpResponseStatusLine httpResponseStatusLine = DEFAULT_RESPONSE_STATUS;
    private String body;

    public void notAllowedMethod() {
        forwardTo(HttpStatus.METHOD_NOT_ALLOWED, DEFAULT_METHOD_NOT_ALLOWED_RESOURCE);
    }

    public void notFound() {
        forwardTo(HttpStatus.NOT_FOUND, DEFAULT_NOT_FOUND_RESOURCE);
    }

    public void redirectTo(final String path) {
        this.httpResponseStatusLine = HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, HttpStatus.FOUND);
        this.httpHeaders.addHeader("Location", path);
        noContentResponse();
    }

    public void forwardTo(final String path) {
        forwardTo(HttpStatus.OK, path);
    }

    public void forwardTo(final HttpStatus httpStatus, final String path) {
        try {
            this.httpResponseStatusLine = HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, httpStatus);
            final View resourceView = StaticResourceView.of(path);
            setHeader("Content-Type", resourceView.getContentType());
            setBody(resourceView.renderView());
        } catch (final IllegalArgumentException exception) {
            notFound();
        }
    }

    public void textPlain(final String simpleTextData) {
        this.httpResponseStatusLine = HttpResponseStatusLine.of(SUPPORTED_HTTP_VERSION, HttpStatus.OK);
        final View simpleStringDataView = SimpleStringDataView.from(simpleTextData);
        setHeader("Content-Type", simpleStringDataView.getContentType());
        setBody(simpleStringDataView.renderView());
    }

    private void noContentResponse() {
        this.body = NO_CONTENT;
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
        httpHeaders.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void setHeader(final String header, final String value) {
        httpHeaders.addHeader(header, value);
    }

    public void setCookie(final String cookieKey, final String cookieValue) {
        httpHeaders.addHeader("Set-Cookie", cookieKey + "=" + cookieValue);
    }
}
