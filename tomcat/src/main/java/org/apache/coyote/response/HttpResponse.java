package org.apache.coyote.response;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.HttpVersion;

public class HttpResponse {

    private static final String SPACE = " ";
    private static final String ENTER = "\r\n";
    private static final String HEADER_SPLIT_DELIMITER = ":";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Headers httpHeaders;
    private final ResponseBody responseBody;

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final Headers httpHeaders, final ResponseBody responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.responseBody = responseBody;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public Headers httpHeaders() {
        return httpHeaders;
    }

    public ResponseBody responseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return "HttpResponse{" + System.lineSeparator() +
               "    httpVersion = " + httpVersion + ", " + System.lineSeparator() +
               "    httpStatus = " + httpStatus + ", " + System.lineSeparator() +
               "    httpHeaders = " + httpHeaders + ", " + System.lineSeparator() +
               "    responseBody = " + responseBody + ", " + System.lineSeparator() +
               '}';
    }
}
