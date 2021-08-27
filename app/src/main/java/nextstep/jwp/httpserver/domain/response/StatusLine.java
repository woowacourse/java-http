package nextstep.jwp.httpserver.domain.response;

import nextstep.jwp.httpserver.domain.HttpVersion;
import nextstep.jwp.httpserver.domain.StatusCode;

public class StatusLine {
    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    public StatusLine(HttpVersion httpVersion, StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
