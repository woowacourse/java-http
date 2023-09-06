package org.apache.coyote.http11;

import org.apache.coyote.http11.session.Cookie;

public class HttpResponse {

    private final ResponseInfo responseInfo;
    private final HttpHeader httpHeader;
    private final ResponseBody responseBody;
    private final Cookie cookie;

    private HttpResponse(final ResponseInfo responseInfo, final HttpHeader httpHeader,
                         final ResponseBody responseBody, final Cookie cookie) {
        this.responseInfo = responseInfo;
        this.httpHeader = httpHeader;
        this.responseBody = responseBody;
        this.cookie = cookie;
    }

    public static HttpResponse defaultResponse() {
        final ResponseInfo responseInfo = ResponseInfo.defaultResponse();
        final HttpHeader httpHeader = HttpHeader.emptyHeader();
        final ResponseBody responseBody = ResponseBody.defaultBody();
        final Cookie cookie = Cookie.emptyCookie();
        return new HttpResponse(responseInfo, httpHeader, responseBody, cookie);
    }

    public void updateRedirect(final HttpVersion httpVersion, final String requestUri) {
        responseInfo.updateResponseInfo(httpVersion, HttpStatus.FOUND);
        httpHeader.addHeader("Location: ", requestUri);
    }

    public void addCookie(final String key, final String value) {
        cookie.addCookie(key, value);
    }

    public ResponseInfo getInfo() {
        return responseInfo;
    }

    public boolean isNotEmptyHeader() {
        return !httpHeader.isEmpty();
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public boolean isNotEmptyBody() {
        return !responseBody.isEmpty();
    }

    public byte[] getBody() {
        return responseBody.getBody();
    }

    public void wrapUp() {
        final byte[] body = responseBody.getBody();
        httpHeader.addHeader("Content-Type", "text/html;charset=utf-8");
        httpHeader.addHeader("Content-Length", String.valueOf(body.length));
    }

    public void updatePage(final String page) {
        responseBody.changeBody(page);
    }
}
