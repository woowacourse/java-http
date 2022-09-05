package org.apache.http;

public class ResponseEntity {

    private static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP11;
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.OK;
    private static final HttpMime DEFAULT_CONTENT_TYPE = HttpMime.DEFAULT;
    private static final int DEFAULT_CONTENT_LENGTH = 0;
    private static final String DEFAULT_CONTENT = "";

    private HttpVersion httpVersion = DEFAULT_HTTP_VERSION;
    private HttpStatus httpStatus = DEFAULT_HTTP_STATUS;
    private HttpMime contentType = DEFAULT_CONTENT_TYPE;
    private int contentLength = DEFAULT_CONTENT_LENGTH;
    private String content = DEFAULT_CONTENT;

    public ResponseEntity httpVersion(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public ResponseEntity httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseEntity contentType(final HttpMime contentType) {
        this.contentType = contentType;
        return this;
    }

    public ResponseEntity content(final String content) {
        this.content = content;
        this.contentLength = content.getBytes().length;
        return this;
    }

    public String build() {
        return String.join("\r\n",
                httpVersion.getValue() + " " + httpStatus.getCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "",
                content);
    }
}
