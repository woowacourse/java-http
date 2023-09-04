package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;

public class ResponseEntity {

    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;
    private final StatusLine statusLine;

    private ResponseEntity(ResponseHeaders responseHeaders, ResponseBody responseBody, StatusLine statusLine) {
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        this.statusLine = statusLine;
    }

    public static ResponseEntity of(ResponseBody responseBody, HttpStatusCode statusCode) {
        ResponseHeaders responseHeaders = ResponseHeaders.of(responseBody);
        StatusLine statusLine = new StatusLine(statusCode);
        return new ResponseEntity(responseHeaders, responseBody, statusLine);
    }

    public static ResponseEntity redirect(String location, HttpStatusCode httpStatusCode) {
        ResponseHeaders responseHeaders = ResponseHeaders.ofRedirect(location);
        StatusLine statusLine = new StatusLine(httpStatusCode);
        return new ResponseEntity(responseHeaders, ResponseBody.EMPTY, statusLine);
    }

    public void addCookie(HttpCookie cookie) {
        responseHeaders.addCookie(cookie);
    }

    @Override
    public String toString() {
        return statusLine + "\r\n" +
                responseHeaders + "\r\n" +
                responseBody.getContent();
    }
}
