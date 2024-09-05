package com.techcourse.model;

public class StatusLine {

    private static final String HTTP11 = "HTTP/1.1";
    private static final String DELIMITER = " ";
    private static final String CRLF = "\r\n";

    private final String version;
    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.version = HTTP11;
        this.httpStatus = httpStatus;
    }

    public StatusLine(String version, HttpStatus httpStatus) {
        this.version = version;
        this.httpStatus = httpStatus;
    }

    public String toResponse() {
        return String.join(DELIMITER,
                version,
                String.valueOf(httpStatus.getStatusCode()),
                httpStatus.getStatusMessage(), CRLF);
    }
}
