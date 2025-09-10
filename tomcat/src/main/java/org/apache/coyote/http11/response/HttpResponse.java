package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Headers;

public class HttpResponse {

    private ContentType contentType = ContentType.NONE;
    private HttpStatus status = HttpStatus.OK;
    private Headers headers = new Headers();
    private String body = "";
    private Cookies cookies = new Cookies();

    public ContentType getContentType() {
        return contentType;
    }

    public boolean isStaticPage() {
        return contentType.isText() && !status.is3xx();
    }

    public String buildResponse() {
        int bodyLength = getBodyLength(getBody());
        return "HTTP/1.1 " + getStatus().getCode() + " " + getStatus().getName() + "\r\n"
            + "Content-Type: " + getContentType().getType() + ";charset=utf-8" + "\r\n"
            + "Content-Length: " + bodyLength + "\r\n"
            + addIfNotEmpty(getHeaders().toString())
            + addIfNotEmpty(getCookies().toString())
            + "\r\n" + getBody();
    }

    private String addIfNotEmpty(String value) {
        if (value.isEmpty()) {
            return "";
        }
        return value + "\r\n";
    }

    private int getBodyLength(String responseBody) {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HttpResponse() {
    }

    private HttpResponse(ContentType contentType, HttpStatus status, Headers headers, String body, Cookies cookies) {
        this.contentType = contentType;
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.cookies = cookies;
    }

    public static class Builder {

        private ContentType contentType = ContentType.NONE;
        private HttpStatus status = HttpStatus.OK;
        private Headers headers = new Headers();
        private String body = "";
        private Cookies cookies = new Cookies();

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder cookie(String key, String value) {
            this.cookies.put(key, value);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(contentType, status, headers, body, cookies);
        }
    }
}
