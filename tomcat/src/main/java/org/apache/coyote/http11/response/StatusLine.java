package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.HttpVersion;

public class StatusLine {

    private final HttpVersion version;
    private final HttpStatusCode statusCode;

    public StatusLine(HttpVersion version, HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String getReponseString() {
        return version.getVersionString() + " " + statusCode.toStatus() + " ";
    }
}
