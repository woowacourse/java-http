package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.common.HttpVersion;

public class HttpStatusLine {

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;

    private HttpStatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static HttpStatusLine of(HttpVersion httpVersion, HttpStatus httpStatus) {
        return new HttpStatusLine(httpVersion, httpStatus);
    }

    public static HttpStatusLine createDefaultStatusLine() {
        return new HttpStatusLine(HttpVersion.V1_1, HttpStatus.OK);
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
