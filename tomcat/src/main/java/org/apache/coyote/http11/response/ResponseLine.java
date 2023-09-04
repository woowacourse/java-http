package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class ResponseLine {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String BLANK = " ";

    private final String httpVersion;
    private final String statusCode;
    private final String statusText;

    private ResponseLine(final String httpVersion, final String statusCode, final String statusText) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static ResponseLine create(final HttpStatus httpStatus) {
        final String statusCode = String.valueOf(HttpStatus.OK.getValue());
        final String statusText = HttpStatus.OK.getReasonPhrase();

        return new ResponseLine(HTTP_VERSION, statusCode, statusText);
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
