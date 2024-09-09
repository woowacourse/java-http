package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.HttpStatusCode;

public class HttpStatusLine {

    private final String version;
    private final HttpStatusCode httpStatusCode;

    public HttpStatusLine(String version, HttpStatusCode httpStatusCode) {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
    }

    public String getString() {
        return version + " " + httpStatusCode.getCode() + " "
                + httpStatusCode.getMessage();
    }

    public String getVersion() {
        return version;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
