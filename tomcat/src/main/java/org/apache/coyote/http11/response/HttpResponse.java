package org.apache.coyote.http11.response;

import common.http.ContentType;
import common.http.Cookie;
import common.http.HttpStatus;
import common.http.Response;

import static org.apache.Constants.CRLF;

public class HttpResponse implements Response {

    private final HttpStatusLine httpStatusLine;
    private final HttpResponseHeaders httpResponseHeaders;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse() {
        this.httpStatusLine = new HttpStatusLine();
        this.httpResponseHeaders = new HttpResponseHeaders();
        this.httpResponseBody = new HttpResponseBody();
    }

    public void addVersionOfTheProtocol(String versionOfTheProtocol) {
        httpStatusLine.addVersionOfTheProtocol(versionOfTheProtocol);
    }

    public void addHttpStatus(HttpStatus httpStatus) {
        httpStatusLine.addHttpStatus(httpStatus);
    }

    public void addContentType(ContentType contentType) {
        httpResponseHeaders.addHeaderFieldAndValue("Content-Type", contentType.getType() + ";charset=utf-8");
    }

    public void addCookie(Cookie cookie) {
        httpResponseHeaders.addHeaderFieldAndValue("Set-Cookie", cookie.getValue());
    }

    public void sendRedirect(String redirectURL) {
        httpResponseHeaders.addHeaderFieldAndValue("Location", redirectURL);
    }

    public void addStaticResourcePath(String name) {
        httpResponseHeaders.addHeaderFieldAndValue("Resource-Path", name);
    }

    public void addBody(String body) {
        httpResponseHeaders.addHeaderFieldAndValue("Content-Length", String.valueOf(body.getBytes().length));
        httpResponseBody.addBody(body);
    }

    public boolean hasStaticResourcePath() {
        return httpResponseHeaders.hasStaticResourcePath();
    }

    public String getStaticResourcePath() {
        return httpResponseHeaders.getStaticResourcePath();
    }

    public String getMessage() {
        if (httpResponseBody.exist()) {
            httpResponseBody.validateLength(httpResponseHeaders.getContentLength());
            return String.join(CRLF,
                    httpStatusLine.getMessage(),
                    httpResponseHeaders.getMessage(),
                    httpResponseBody.getMessage());
        }

        return String.join(CRLF,
                httpStatusLine.getMessage(),
                httpResponseHeaders.getMessage());
    }
}
