package org.apache.coyote.http11.httpmessage.response;

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
}
