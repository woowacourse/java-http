package org.apache.coyote.http11.httpmessage.response;

import java.util.Objects;
import org.apache.coyote.http11.httpmessage.request.Http11Version;

public class StatusLine {

    private final Http11Version http11Version;
    private final HttpStatus httpStatus;


    public StatusLine(Http11Version http11Version, HttpStatus httpStatus) {
        this.http11Version = http11Version;
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                http11Version.getVersion(),
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
        return http11Version == that.http11Version && httpStatus == that.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(http11Version, httpStatus);
    }
}
