package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.common.HttpVersion;

public class HttpStatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public HttpStatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
