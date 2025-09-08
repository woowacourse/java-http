package org.apache.coyote.http11.response;

import org.apache.coyote.http11.domain.ContentType;

public class Http11Response {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";

    private StatusLine statusLine;
    private ResponseHeaders headers;
    private ResponseBody body;

    public Http11Response() {
        this.statusLine = new StatusLine();
        this.headers = new ResponseHeaders();
        this.body = new ResponseBody(0);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setContentType(final String resourcePath) {
        ContentType contentType = ContentType.fromPath(resourcePath);
        this.addHeader(CONTENT_TYPE, contentType.getValue());
    }

    public void setContentLength() {
        int length = body.bytes().length;
        this.addHeader(CONTENT_LENGTH, String.valueOf(length));
    }


    public byte[] getResponseLine() {
        return statusLine.getBytes();
    }

    public byte[] getHeader() {
        return headers.getHeader();
    }

    public void setState(HttpStatus state) {
        this.statusLine = new StatusLine(this.statusLine.protocol(), state);
    }

    public HttpStatus getState() {
        return this.statusLine.status();
    }

    public byte[] getBody() {
        return body.bytes();
    }

    public void setBody(byte[] body) {
        this.body = new ResponseBody(body);
    }
}
