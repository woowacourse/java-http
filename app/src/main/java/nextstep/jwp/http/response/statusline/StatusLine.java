package nextstep.jwp.http.response.statusline;

import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.common.HttpVersion;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine from(HttpStatus httpStatus) {
        return new StatusLine(HttpVersion.HTTP_1_1, httpStatus);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s ",
            httpVersion.getValue(), httpStatus.getCodeString(), httpStatus.getReasonPhrase());
    }
}
