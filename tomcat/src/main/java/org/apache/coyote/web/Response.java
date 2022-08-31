package org.apache.coyote.web;

import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public abstract class Response {

    private static final String DEFAULT_VERSION = "HTTP/1.1";
    protected static final String HEADER_TEMPLATE = "%s: %s \r\n";

    private final String version;
    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;

    public Response(final HttpStatus httpStatus, final HttpHeaders httpHeaders) {
        this(DEFAULT_VERSION, httpStatus, httpHeaders);
    }

    public Response(final String version, final HttpStatus httpStatus, final HttpHeaders httpHeaders) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
    }

    public abstract String createHttpResponse();

    protected String getRequestLine() {
        return String.format("%s %d %s \r\n", version, httpStatus.getStatusCode(), httpStatus.getMessage());
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
