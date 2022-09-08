package org.apache.coyote.domain.response;

import org.apache.coyote.domain.request.requestline.HttpVersion;

public class ResponseLine {
    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;

    private ResponseLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public static ResponseLine of(final HttpVersion httpVersion, final HttpStatusCode httpStatusCode) {
        return new ResponseLine(httpVersion, httpStatusCode);
    }

    public String generateResponseString() {
        return httpVersion.getMessage() + " "
                + httpStatusCode.getStatusCode() + " "
                + httpStatusCode.getStatusMessage();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
