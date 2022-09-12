package org.apache.coyote.http11.httpmessage.response;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.Headers;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpVersion;
import org.apache.coyote.http11.session.Cookie;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Headers headers;
    private final ResponseBody responseBody;

    public HttpResponse(StatusLine statusLine, Headers headers, ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse from(HttpRequest httpRequest) {
        HttpVersion httpVersion = httpRequest.getHttpVersion();
        StatusLine statusLine = StatusLine.from(httpVersion);
        Headers headers = new Headers(new LinkedHashMap<>());
        ResponseBody responseBody = new ResponseBody("");

        return new HttpResponse(statusLine, headers, responseBody);
    }

    public HttpResponse ok(String body) {
        return ok(ContentType.HTML, body);
    }

    public HttpResponse ok(ContentType contentType, String body) {
        return this.httpStatus(HttpStatus.OK)
                .content(contentType, body.getBytes().length)
                .responseBody(body);
    }

    public HttpResponse found(String path) {
        return this.httpStatus(HttpStatus.FOUND)
                .location(path)
                .content(ContentType.HTML, 0);
    }

    public HttpResponse badRequest() {
        return this.httpStatus(HttpStatus.FOUND)
                .location("/400.html")
                .content(ContentType.HTML, 0)
                .responseBody("");
    }

    public HttpResponse unAuthorized() {
        return this.httpStatus(HttpStatus.FOUND)
                .location("/401.html")
                .content(ContentType.HTML, 0)
                .responseBody("");
    }

    public HttpResponse notFound() {
        return this.httpStatus(HttpStatus.FOUND)
                .location("/404.html")
                .content(ContentType.HTML, 0);
    }

    public HttpResponse Error() {
        return this.httpStatus(HttpStatus.FOUND)
                .location("/500.html")
                .content(ContentType.HTML, 0);
    }

    public HttpResponse httpStatus(HttpStatus httpStatus) {
        statusLine.addHttpStatus(httpStatus);
        return this;
    }

    private HttpResponse location(String path) {
        headers.addLocation(path);
        return this;
    }

    private HttpResponse content(ContentType contentType, int length) {
        return this.contentType(contentType)
                .contentLength(length);
    }

    private HttpResponse contentType(ContentType contentType) {
        headers.addContentType(contentType);
        return this;
    }

    private HttpResponse contentLength(int length) {
        headers.addContentLength(length);
        return this;
    }

    public HttpResponse setCookie(Cookie cookie) {
        headers.addSetCookie(cookie);
        return this;
    }

    public HttpResponse responseBody(String body) {
        responseBody.addBody(body);
        return this;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                statusLine.toString(),
                headers.toString(),
                "",
                responseBody.toString()
        );
    }
}
