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

    @Override
    public void addVersionOfTheProtocol(String versionOfTheProtocol) {
        httpStatusLine.addVersionOfTheProtocol(versionOfTheProtocol);
    }

    @Override
    public void addHttpStatus(HttpStatus httpStatus) {
        httpStatusLine.addHttpStatus(httpStatus);
    }

    @Override
    public void addContentType(ContentType contentType) {
        httpResponseHeaders.addHeaderFieldAndValue("Content-Type", contentType.getType() + ";charset=utf-8");
    }

    @Override
    public void addCookie(Cookie cookie) {
        httpResponseHeaders.addHeaderFieldAndValue("Set-Cookie", cookie.getValue());
    }

    @Override
    public void sendRedirect(String redirectURL) {
        httpResponseHeaders.addHeaderFieldAndValue("Location", redirectURL);
    }

    @Override
    public void addStaticResourcePath(String name) {
        httpResponseHeaders.addHeaderFieldAndValue("Resource-Path", name);
    }

    @Override
    public void addBody(String body) {
        httpResponseHeaders.addHeaderFieldAndValue("Content-Length", String.valueOf(body.getBytes().length));
        httpResponseBody.addBody(body);
    }

    @Override
    public boolean hasStaticResourcePath() {
        return httpResponseHeaders.hasStaticResourcePath();
    }

    @Override
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
