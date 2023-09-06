package nextstep.jwp.http;

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
