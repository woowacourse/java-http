package org.apache.coyote.http;

public class ResponseHeader {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Header header;

    public ResponseHeader() {
        this(new Header());
    }

    private ResponseHeader(Header header) {
        this.header = header;
    }

    public void setContentType(String contentType) {
        header.addHeader(CONTENT_TYPE, contentType);
    }

    public void setContentLength(long contentLength) {
        header.addHeader(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public String toResponse() {
        return header.toResponse();
    }

    public static ResponseHeader basicResponseHeader(int length) {
        Header header = new Header();
        header.addHeader(CONTENT_TYPE, "text/plain");
        header.addHeader(CONTENT_LENGTH, String.valueOf(length));
        return new ResponseHeader(header);
    }
}
