package org.apache.catalina.response;

import org.apache.catalina.http.VersionOfProtocol;

public record StatusLine(VersionOfProtocol versionOfProtocol, HttpStatus httpStatus) {

    @Override
    public String toString() {
        return versionOfProtocol + " " + httpStatus;
    }
}
