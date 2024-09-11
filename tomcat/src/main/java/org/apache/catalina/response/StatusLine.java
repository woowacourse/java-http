package org.apache.catalina.response;

import org.apache.catalina.http.VersionOfProtocol;

public class StatusLine {
    private final VersionOfProtocol versionOfProtocol;
    private final HttpStatus httpStatus;

    public StatusLine(VersionOfProtocol versionOfProtocol, HttpStatus httpStatus) {
        this.versionOfProtocol = versionOfProtocol;
        this.httpStatus = httpStatus;
    }

    public VersionOfProtocol getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
