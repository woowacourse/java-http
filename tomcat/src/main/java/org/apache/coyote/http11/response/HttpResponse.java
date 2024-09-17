package org.apache.coyote.http11.response;

import org.apache.coyote.http11.MimeType;

import java.util.Map;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader requestHeader;
    private ResponseBody responseBody;

    public HttpResponse() {
        this(null, new ResponseHeader(), null);
    }

    public HttpResponse(HttpStatus httpStatus, ResponseHeader responseHeader, byte[] contents) {
        this.statusLine = StatusLine.of11(httpStatus);
        this.requestHeader = responseHeader;
        this.responseBody = new ResponseBody(contents);

        setContentLength(responseBody.getLength());
    }

    public void redirectTo(String location) {
        setStatus(HttpStatus.FOUND);
        setLocation(location);
        setMimeType(MimeType.from(location));
    }

    public void setStatus(HttpStatus httpStatus) {
        statusLine.setHttpStatus(httpStatus);
    }

    public void setContentLength(int contentLength) {
        requestHeader.setContentLength(String.valueOf(contentLength));
    }

    public void setMimeType(MimeType mimeType) {
        requestHeader.setContentType(mimeType);
    }

    public void setCookie(String cookie) {
        requestHeader.setCookie(cookie);
    }

    public void setLocation(String location) {
        requestHeader.setLocation(location);
    }

    public void setResponseBody(byte[] values) {
        this.responseBody = new ResponseBody(values);
        setContentLength(responseBody.getLength());
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader.getFields();
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
