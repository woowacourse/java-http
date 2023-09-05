package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class StatusLine {

    private HttpStatusCode statusCode;

    public StatusLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static StatusLine ok() {
        return new StatusLine(HttpStatusCode.OK);
    }

    public void setStatusCode(HttpStatusCode httpStatusCode) {
        this.statusCode = httpStatusCode;
    }

    public String parse() {
        return HttpVersion.HTTP_1_1.getVersion() + " " + statusCode.getCode() + " " + statusCode.getName() + " ";
    }
}
