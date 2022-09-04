package org.apache.http;

import java.util.Objects;

public class ResponseEntity {

    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CONTENT_TYPE = "*/*";

    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final String contentType;
    private final String content;
    private final int contentLength;

    public ResponseEntity(final HttpStatus httpStatus, final String content) {
        this(DEFAULT_HTTP_VERSION, httpStatus, DEFAULT_CONTENT_TYPE, content);
    }

    public ResponseEntity(final HttpStatus httpStatus, final String contentType, final String content) {
        this(DEFAULT_HTTP_VERSION, httpStatus, contentType, content);
    }

    public ResponseEntity(final String httpVersion, final HttpStatus httpStatus, final String contentType, final String content) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.content = parseContent(content);
        this.contentLength = calculateContentLength(this.content);
    }

    private String parseContent(final String content) {
        if (Objects.isNull(content)) {
            return "";
        }
        return content;
    }

    private int calculateContentLength(final String content) {
        return content.getBytes()
                .length;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public int getContentLength() {
        return contentLength;
    }
}
