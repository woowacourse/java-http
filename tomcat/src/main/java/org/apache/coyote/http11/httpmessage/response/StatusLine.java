package org.apache.coyote.http11.httpmessage.response;

import java.util.Objects;
import org.apache.coyote.http11.httpmessage.request.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    private StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine from(HttpVersion httpVersion) {
        return new StatusLine(httpVersion, null);
    }

    public void addHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                httpVersion.getVersion(),
                Integer.toString(httpStatus.getValue()),
                httpStatus.getMessage(),
                ""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusLine that = (StatusLine) o;
        return httpVersion == that.httpVersion && httpStatus == that.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpVersion, httpStatus);
    }
}
