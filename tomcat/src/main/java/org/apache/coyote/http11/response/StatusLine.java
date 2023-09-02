package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class StatusLine {

    private final HttpStatusCode statusCode;

    public StatusLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return HttpVersion.HTTP_1_1.getVersion() + " " + statusCode.getCode() + " " + statusCode.getName() + " ";
    }
}
