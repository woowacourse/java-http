package org.apache.coyote.http11.model.response;

import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;

public class HttpResponse {

    private final HttpStatusLine statusLine;
    private final HttpResponseHeader headers;
    private String responseBody;

    public HttpResponse() {
        this.statusLine = new HttpStatusLine();
        this.headers = new HttpResponseHeader();
    }

    public static void redirect(HttpResponse response, String page) {
        response.statusCode(HttpStatus.FOUND);
        response.addHeader(HttpHeaderType.LOCATION, page);
    }

    public void statusCode(HttpStatus httpStatus) {
        this.statusLine.setStatus(httpStatus);
    }

    public void addHeader(String headerName, String headerValue) {
        this.headers.addValue(headerName, headerValue);
    }

    public void addCookie(String cookie) {
        this.headers.addCookie(cookie);
    }

    public void responseBody(String responseBody) {
        this.responseBody = responseBody;
        int contentLength = responseBody.getBytes().length;
        addHeader(HttpHeaderType.CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public byte[] toResponse() {
        if (responseBody == null) {
            return String.join("\r\n",
                    statusLine.toResponse(),
                    headers.toResponse()).getBytes();
        }
        return String.join("\r\n",
                statusLine.toResponse(),
                headers.toResponse(),
                responseBody).getBytes();
    }

    public String getProtocolVersion() {
        return statusLine.getProtocolVersion();
    }

    public HttpStatus getStatusCode() {
        return statusLine.getHttpStatus();
    }

    public String getHeader(String headerName) {
        return headers.getHeader(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
