package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.HttpStatusCode;

public class HttpStatusLine {

    private static final String STATUS_LINE_DELIMITER = " ";

    private final String version;
    private final HttpStatusCode httpStatusCode;

    public HttpStatusLine(String version, HttpStatusCode httpStatusCode) {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
    }

    public String getString() {
        return String.join(
                STATUS_LINE_DELIMITER,
                version,
                String.valueOf(httpStatusCode.getCode()),
                httpStatusCode.getMessage()
        );
    }

    public String getVersion() {
        return version;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
