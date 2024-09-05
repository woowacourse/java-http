package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpVersion;

import static org.apache.coyote.http.Constants.CRLF;
import static org.apache.coyote.http.Constants.SPACE;

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

    public String toResponse() {
        return String.join(SPACE,
                version.getVersion(),
                String.valueOf(httpStatus.getStatusCode()),
                httpStatus.getStatusMessage(), CRLF);
    }
}
