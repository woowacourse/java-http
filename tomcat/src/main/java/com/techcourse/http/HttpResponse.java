package com.techcourse.http;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpStatusCode httpStatusCode;
    private HttpHeaders headers;
    private String body;

    public HttpResponse() {
        this(HttpStatusCode.OK, new HttpHeaders(), "");
    }

    public HttpResponse ok(String body) {
        httpStatusCode = HttpStatusCode.OK;
        this.body = body;
        return this;
    }

    public HttpResponse found(String location) {
        httpStatusCode = HttpStatusCode.FOUND;
        headers.setLocation(location);
        return this;
    }

    public HttpResponse badRequest() {
        httpStatusCode = HttpStatusCode.BAD_REQUEST;
        return this;
    }

    public HttpResponse notFound() {
        httpStatusCode = HttpStatusCode.NOT_FOUND;
        return this;
    }

    public HttpResponse internalServerError() {
        httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
        return this;
    }

    public byte[] build() {
        if (!body.isBlank()) {
            headers.setContentLength(body.getBytes().length);
        }
        return "%s %d %s \r\n%s\r\n%s"
                .formatted(
                        HTTP_VERSION,
                        httpStatusCode.getCode(),
                        httpStatusCode.getMessage(),
                        headers.getHeadersString(),
                        body
                ).getBytes();
    }

    public void clear() {
        headers.clear();
        body = "";
    }

    public HttpResponse setStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
        return this;
    }

    public HttpResponse setHeader(String key, String value) {
        headers.set(key, value);
        return this;
    }

    public HttpResponse setLocation(String location) {
        headers.setLocation(location);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        headers.setContentType(contentType);
        return this;
    }

    public HttpResponse setCookie(String key, String value) {
        headers.setCookie(key, value);
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public void setHttpOnly(boolean httpOnly) {
        headers.setHttpOnly(httpOnly);
    }
}
