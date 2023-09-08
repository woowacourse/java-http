package org.apache.coyote.http11.response.statusLine;

import org.apache.coyote.http11.common.HttpVersion;

import java.util.Objects;

public class StatusLine {

    private static final String SPACE = " ";

    private final HttpVersion httpVersion;
    private final int statusCode;
    private final String statusMessage;

    private StatusLine(final HttpVersion httpVersion, final int statusCode, final String statusMessage) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static StatusLine of(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        return new StatusLine(httpVersion, httpStatus.getCode(), httpStatus.getMessage());
    }

    public String convertToString() {
        return new StringBuilder().append(httpVersion.getName()).append(SPACE)
                                  .append(statusCode).append(SPACE)
                                  .append(statusMessage).append(SPACE)
                                  .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StatusLine that = (StatusLine) o;
        return statusCode == that.statusCode && httpVersion == that.httpVersion && Objects.equals(statusMessage, that.statusMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpVersion, statusCode, statusMessage);
    }

    @Override
    public String toString() {
        return "StatusLine{" +
                "httpVersion=" + httpVersion +
                ", statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
