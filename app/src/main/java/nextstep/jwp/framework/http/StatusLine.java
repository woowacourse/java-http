package nextstep.jwp.framework.http;

import java.util.Objects;

public class StatusLine {
    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;

    public StatusLine(HttpVersion httpVersion, HttpStatus httpStatus) {
        this.httpVersion = Objects.requireNonNull(httpVersion);
        this.httpStatus = Objects.requireNonNull(httpStatus);
    }

    public String getHttpVersion() {
        return httpVersion.getVersion();
    }

    public int getHttpStatusCode() {
        return httpStatus.getCode();
    }

    public String getReasonPhrase() {
        return httpStatus.getReasonPhrase();
    }
}
