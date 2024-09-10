package org.apache.coyote.http11.component.response;

import org.apache.coyote.http11.component.common.Version;

public class ResponseLine {

    public static final ResponseLine OK = new ResponseLine(new Version(1, 1), new StatusCode("OK", 200));

    private final Version version;
    private final StatusCode statusCode;

    public ResponseLine(final Version version, final StatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String getResponseText() {
        return version.getResponseText() + " " + statusCode.getResponseText() + " ";
    }
}
