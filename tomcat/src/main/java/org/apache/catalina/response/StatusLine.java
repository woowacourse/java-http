package org.apache.catalina.response;

import java.util.Objects;

import org.apache.catalina.http.VersionOfProtocol;

public final class StatusLine {
    private final VersionOfProtocol versionOfProtocol;
    private HttpStatus httpStatus;

    public StatusLine(VersionOfProtocol versionOfProtocol, HttpStatus httpStatus) {
        this.versionOfProtocol = versionOfProtocol;
        this.httpStatus = httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return versionOfProtocol + " " + httpStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (StatusLine) obj;
        return Objects.equals(this.versionOfProtocol, that.versionOfProtocol) &&
                Objects.equals(this.httpStatus, that.httpStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionOfProtocol, httpStatus);
    }

}
