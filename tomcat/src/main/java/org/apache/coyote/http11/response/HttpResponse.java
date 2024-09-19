package org.apache.coyote.http11.response;

import org.apache.coyote.http11.MimeType;

import java.util.Map;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse() {
        this(null, new ResponseHeader(), null);
    }

    public HttpResponse(HttpStatus httpStatus, ResponseHeader responseHeader, byte[] contents) {
        this.statusLine = StatusLine.of11(httpStatus);
        this.responseHeader = responseHeader;
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
        responseHeader.setContentLength(String.valueOf(contentLength));
    }

    public void setMimeType(MimeType mimeType) {
        responseHeader.setContentType(mimeType);
    }

    public void setCookie(String cookie) {
        responseHeader.setCookie(cookie);
    }

    public void setLocation(String location) {
        responseHeader.setLocation(location);
    }

    public void setResponseBody(byte[] values) {
        this.responseBody = new ResponseBody(values);
        setContentLength(responseBody.getLength());
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader.getFields();
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
