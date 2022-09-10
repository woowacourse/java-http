package org.apache.coyote.domain.response.statusline;

import org.apache.coyote.domain.request.requestline.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;

    private StatusLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public static StatusLine of(final HttpVersion httpVersion, final HttpStatusCode httpStatusCode) {
        return new StatusLine(httpVersion, httpStatusCode);
    }

    public String generateResponseString() {
        return "\r\n" + httpVersion.getMessage() + " "
                + httpStatusCode.getCode() + " "
                + httpStatusCode.getMessage() + "\r\n";
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
