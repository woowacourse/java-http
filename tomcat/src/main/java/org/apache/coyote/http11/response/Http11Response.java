package org.apache.coyote.http11.response;

import org.apache.coyote.http11.domain.ContentType;

public class Http11Response {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final ResponseBody body;

    public Http11Response() {
        this.statusLine = new StatusLine();
        this.headers = new ResponseHeaders();
        this.body = new ResponseBody(0);
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public void addCookie(final String key, final String value) {
        headers.addCookie(key, value);
    }

    public void setContentType(final String resourcePath) {
        final ContentType contentType = ContentType.fromPath(resourcePath);
        this.addHeader(CONTENT_TYPE, contentType.getValue());
    }

    public void setContentLength() {
        final int length = body.getBytes().length;
        this.addHeader(CONTENT_LENGTH, String.valueOf(length));
    }

    public byte[] getResponseLine() {
        return statusLine.getBytes();
    }

    public byte[] getHeader() {
        return headers.getHeader();
    }

    public void setState(final HttpStatus setStatus) {
        this.statusLine.setStatus(setStatus);
    }

    public HttpStatus getState() {
        return this.statusLine.getStatus();
    }

    public byte[] getBody() {
        return body.getBytes();
    }

    public void setBody(final byte[] bytes) {
        this.body.setBytes(bytes);
    }

    public HttpStatus getHttpStatus() {
        return statusLine.getStatus();
    }
}
