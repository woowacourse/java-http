package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.exception.HttpParseException;
import org.apache.coyote.http11.HttpHeaders;


public class HttpResponse {
    private static final String SUPPORTED_VERSION = "HTTP/1.1";

    private final String version;
    private final HttpStatus status;
    private final HttpHeaders headers;

    public HttpResponse(String version, HttpStatus status) {
        validateVersion(version);
        this.version = version;
        this.status = status;
        this.headers = HttpHeaders.empty();
    }

    public HttpResponse(String version, HttpStatus status, HttpHeaders headers) {
        validateVersion(version);
        this.version = version;
        this.status = status;
        this.headers = headers;
    }

    private void validateVersion(String version) {
        if (!SUPPORTED_VERSION.equals(version)) {
            throw new HttpParseException("지원하지 않는 HTTP 버전입니다: " + version);
        }
    }
}
