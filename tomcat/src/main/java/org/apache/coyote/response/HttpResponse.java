package org.apache.coyote.response;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.session.Cookies;

import java.util.List;
import java.util.Objects;

public class HttpResponse {

    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private HttpStatus httpStatus = HttpStatus.OK;
    private ResponseHeaders responseHeaders = ResponseHeaders.empty();
    private ResponseBody responseBody = ResponseBody.empty();

    private HttpResponse() {
    }

    public static HttpResponse empty() {
        return new HttpResponse();
    }

    public String getHeaderValue(final String headerName) {
        return this.responseHeaders.getHeaderValue(headerName);
    }

    public List<String> headerNames() {
        return this.responseHeaders.headerNames();
    }

    public HttpResponse setHttpVersion(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse setContentType(final String contentType) {
        this.responseHeaders.setContentType(contentType);
        return this;
    }

    public HttpResponse setContentLength(final int contentLength) {
        this.responseHeaders.setContentLength(contentLength);
        return this;
    }

    public HttpResponse sendRedirect(final String uri) {
        this.httpStatus = HttpStatus.FOUND;
        this.responseHeaders.setLocation(uri);
        return this;
    }

    public HttpResponse setCookies(final Cookies cookies) {
        this.responseHeaders.setCookies(cookies);
        return this;
    }
    public HttpResponse setResponseBody(final ResponseBody responseBody) {
        this.responseHeaders.setContentLength(responseBody.length());
        this.responseBody = responseBody;
        return this;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public ResponseBody responseBody() {
        return responseBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpResponse that = (HttpResponse) o;
        return Objects.equals(responseHeaders, that.responseHeaders) && Objects.equals(responseBody, that.responseBody) && httpVersion == that.httpVersion && httpStatus == that.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseHeaders, responseBody, httpVersion, httpStatus);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
               "responseHeaders=" + responseHeaders +
               ", responseBody=" + responseBody +
               ", httpVersion=" + httpVersion +
               ", httpStatus=" + httpStatus +
               '}';
    }
}
