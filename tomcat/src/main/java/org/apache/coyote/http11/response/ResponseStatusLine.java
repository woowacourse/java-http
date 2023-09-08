package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

public class ResponseStatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String BLANK = " ";

    private final String httpVersion;
    private final String statusCode;
    private final String statusText;

    private ResponseStatusLine(final String httpVersion, final String statusCode, final String statusText) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static ResponseStatusLine create(final HttpStatus httpStatus) {
        final String statusCode = String.valueOf(httpStatus.getValue());
        final String statusText = httpStatus.getReasonPhrase();

        return new ResponseStatusLine(HTTP_VERSION, statusCode, statusText);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    @Override
    public String toString() {
        return httpVersion + BLANK
                + statusCode + BLANK
                + statusText + BLANK;
    }
}
