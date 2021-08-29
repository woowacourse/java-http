package nextstep.jwp.http.response;

import nextstep.jwp.http.HttpVersion;

public class StatusLine {
    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }
}
