package org.apache.tomcat.http.response;

import java.util.Objects;

import org.apache.tomcat.http.common.Version;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StatusLine that = (StatusLine) o;
        return Objects.equals(version, that.version) && Objects.equals(statusCode, that.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, statusCode);
    }
}
