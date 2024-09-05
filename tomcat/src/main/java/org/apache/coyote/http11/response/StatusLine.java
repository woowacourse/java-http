package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatusCode;

public class StatusLine {

    private final String version;
    private final HttpStatusCode statusCode;

    public StatusLine(String version, HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String getReponseString() {
        return version + " " + statusCode.toStatus() + " ";
    }
}
