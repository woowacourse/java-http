package org.apache.coyote.http11.httpmessage.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.Headers;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpVersion;
import org.apache.coyote.http11.session.Cookie;

public class HttpResponse {

    private final OutputStream outputStream;
    private final StatusLine statusLine;
    private final Headers headers;
    private final ResponseBody responseBody;

    private HttpResponse(OutputStream outputStream, StatusLine statusLine, Headers headers, ResponseBody responseBody) {
        this.outputStream = outputStream;
        this.statusLine = statusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    // TODO: 2022/09/09 session까지 확인해주기
    public static HttpResponse of(OutputStream outputStream, HttpRequest httpRequest) {
        HttpVersion httpVersion = httpRequest.getHttpVersion();
        StatusLine statusLine = StatusLine.from(httpVersion);
        Headers headers = new Headers(new LinkedHashMap<>());
        ResponseBody responseBody = new ResponseBody("");

        return new HttpResponse(outputStream, statusLine, headers, responseBody);
    }

    public HttpResponse ok(String body) throws IOException {
        return ok(ContentType.HTML, body);
    }

    public HttpResponse ok(ContentType contentType, String body) throws IOException {
        return this.httpStatus(HttpStatus.OK)
                .content(contentType, body.getBytes().length)
                .responseBody(body);
    }

    public HttpResponse found(String path) {
        return this.httpStatus(HttpStatus.FOUND)
                .location(path)
                .content(ContentType.HTML, 0);
    }

    public HttpResponse unAuthorized() {
        return this.httpStatus(HttpStatus.FOUND)
                .location("/401.html")
                .content(ContentType.HTML, 0)
                .responseBody("");
    }

    public void notFound() {
        this.httpStatus(HttpStatus.FOUND)
                .location("/404.html")
                .content(ContentType.HTML, 0);
    }

    public HttpResponse sendError() {
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

    public void write() throws IOException {
        String result = this.toString();

        outputStream.write(result.getBytes());
        outputStream.flush();
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
