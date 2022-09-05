package org.apache.http;

public class ResponseEntity {

    private static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP11;
    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.OK;

    private final Headers headers;
    private HttpVersion httpVersion = DEFAULT_HTTP_VERSION;
    private HttpStatus httpStatus = DEFAULT_HTTP_STATUS;
    private String content;

    public ResponseEntity(final Headers headers) {
        this.headers = headers;
    }

    public ResponseEntity httpVersion(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public ResponseEntity httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseEntity content(final String content) {
        this.content = content;
        this.headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        return this;
    }

    public String parse() {
        return String.join(
                "\r\n",
                String.format("%s %s %s ", httpVersion.getValue(), httpStatus.getCode(), httpStatus.name()),
                headers.parse(),
                content
        );
    }
}
