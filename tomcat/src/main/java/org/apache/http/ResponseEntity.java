package org.apache.http;

public class ResponseEntity {

    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CONTENT_TYPE = "*/*";
    private static final int DEFAULT_CONTENT_LENGTH = 0;
    private static final String DEFAULT_CONTENT = "";

    private String httpVersion = DEFAULT_HTTP_VERSION;
    private HttpStatus httpStatus = HttpStatus.OK;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private int contentLength = DEFAULT_CONTENT_LENGTH;
    private String content = DEFAULT_CONTENT;

    public ResponseEntity httpVersion(final String httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public ResponseEntity httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseEntity contentType(final String contentType) {
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
                httpVersion + " " + httpStatus.getCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "",
                content);
    }
}
