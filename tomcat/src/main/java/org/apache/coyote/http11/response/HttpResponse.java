package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;

public class HttpResponse {

    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;
    private final StatusLine statusLine;

    private HttpResponse(ResponseHeaders responseHeaders, ResponseBody responseBody, StatusLine statusLine) {
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        this.statusLine = statusLine;
    }

    public static HttpResponse create() {
        ResponseBody responseBody = ResponseBody.EMPTY;
        ResponseHeaders responseHeaders = ResponseHeaders.of(responseBody);
        StatusLine statusLine = StatusLine.ok();
        return new HttpResponse(responseHeaders, responseBody, statusLine);
    }

    public void setResponseBody(ResponseBody responseBody) {
        this.responseHeaders.addContent(responseBody);
        this.responseBody.setContent(responseBody);
    }

    public void addCookie(HttpCookie cookie) {
        this.responseHeaders.addCookie(cookie);
    }

    public void setStatusCode(HttpStatusCode httpStatusCode) {
        this.statusLine.setStatusCode(httpStatusCode);
    }

    public void location(String location) {
        this.responseHeaders.setLocation(location);
    }

    public String parse() {
        return statusLine.parse() + "\r\n" +
                responseHeaders.parse() + "\r\n" +
                responseBody.getContent();
    }
}
