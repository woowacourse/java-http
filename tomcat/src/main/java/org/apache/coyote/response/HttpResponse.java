package org.apache.coyote.response;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.session.Cookies;

import static org.apache.coyote.common.CharacterSet.UTF_8;

public class HttpResponse {

    private Headers headers = Headers.empty();
    private ResponseBody responseBody = ResponseBody.empty();
    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private HttpStatus httpStatus = HttpStatus.OK;

    private HttpResponse() {
    }

    private HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final Headers headers, final ResponseBody responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse empty() {
        return new HttpResponse();
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public Headers httpHeaders() {
        return headers;
    }

    public ResponseBody responseBody() {
        return responseBody;
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
        this.headers.setContentType(contentType + ";" + UTF_8.value());
        return this;
    }

    public HttpResponse setContentLength(final int contentLength) {
        this.headers.setContentLength(contentLength);
        return this;
    }

    public HttpResponse sendRedirect(final String uri) {
        this.httpStatus = HttpStatus.FOUND;
        this.headers.setLocation(uri);
        return this;
    }

    public HttpResponse setCookies(final Cookies cookies) {
        this.headers.setCookies(cookies);
        return this;
    }

    public HttpResponse setResponseBody(final ResponseBody responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    @Override
    public String toString() {
        return "HttpResponse{" + System.lineSeparator() +
               "    httpVersion = " + httpVersion + ", " + System.lineSeparator() +
               "    httpStatus = " + httpStatus + ", " + System.lineSeparator() +
               "    httpHeaders = " + headers + ", " + System.lineSeparator() +
               "    responseBody = " + responseBody + ", " + System.lineSeparator() +
               '}';
    }

    public static class HttpResponseBuilder {

        private Headers headers = Headers.empty();
        private ResponseBody responseBody = ResponseBody.empty();
        private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        private HttpStatus httpStatus;

        public HttpResponseBuilder setHttpVersion(final HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public HttpResponseBuilder setHttpStatus(final HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public HttpResponseBuilder setContentType(final String contentType) {
            this.headers.setContentType(contentType + ";" + UTF_8.value());
            return this;
        }

        public HttpResponseBuilder setContentLength(final int contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        public HttpResponseBuilder sendRedirect(final String uri) {
            this.httpStatus = HttpStatus.FOUND;
            this.headers.setLocation(uri);
            return this;
        }

        public HttpResponseBuilder setCookies(final Cookies cookies) {
            this.headers.setCookies(cookies);
            return this;
        }

        public HttpResponseBuilder addCookie(final String cookieName, final String cookieValue) {
            this.headers.addCookie(cookieName, cookieValue);
            return this;
        }

        public HttpResponseBuilder setResponseBody(final ResponseBody responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(httpVersion, httpStatus, headers, responseBody);
        }
    }
}
