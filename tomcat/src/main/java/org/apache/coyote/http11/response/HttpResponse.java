package org.apache.coyote.http11.response;


import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpCookie;

public class HttpResponse {

    private StatusLine statusLine;
    private Headers headers = Headers.empty();
    private ResponseBody responseBody;

    public HttpResponse statusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponse redirect(String uri) {
        this.headers.add("Location", uri);
        return this;
    }

    public HttpResponse contentType(String contentType) {
        this.headers.add("Content-Type", contentType + ";charset=utf-8");
        return this;
    }

    public HttpResponse contentLength(int contentLength) {
        this.headers.add("Content-Length", String.valueOf(contentLength));
        return this;
    }

    public HttpResponse setCookie(HttpCookie httpCookie) {
        this.headers.setCookie(httpCookie);
        return this;
    }

    public HttpResponse responseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

}
