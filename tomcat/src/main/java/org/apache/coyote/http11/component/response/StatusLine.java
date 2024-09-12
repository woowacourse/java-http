package org.apache.coyote.http11.component.response;

import org.apache.coyote.http11.component.common.Version;

public class StatusLine {

    public static final StatusLine OK = new StatusLine(new Version(1, 1), new StatusCode("OK", 200));
    public static final StatusLine FOUND = new StatusLine(new Version(1, 1), new StatusCode("FOUND", 302));

    private final Version version;
    private final StatusCode statusCode;

    public StatusLine(final Version version, final StatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String getResponseText() {
        return version.getResponseText() + " " + statusCode.getResponseText() + " ";
    }
}
