package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpVersion;

import static org.apache.coyote.util.Constants.CRLF;
import static org.apache.coyote.util.Constants.SPACE;

public class StatusLine {

    private final HttpVersion version;
    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.version = HttpVersion.HTTP11;
        this.httpStatus = httpStatus;
    }

    public StatusLine(HttpVersion version, HttpStatus httpStatus) {
        this.version = version;
        this.httpStatus = httpStatus;
    }

    public boolean needRedirectLocation() {
        return httpStatus.equals(HttpStatus.FOUND);
    }

    public String toResponse() {
        return String.join(SPACE,
                version.getVersion(),
                String.valueOf(httpStatus.getStatusCode()),
                httpStatus.getStatusMessage(), CRLF);
    }
}
