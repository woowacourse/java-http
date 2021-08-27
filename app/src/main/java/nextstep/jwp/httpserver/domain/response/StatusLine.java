package nextstep.jwp.httpserver.domain.response;

import nextstep.jwp.httpserver.domain.HttpVersion;
import nextstep.jwp.httpserver.domain.StatusCode;

public class StatusLine {
    private HttpVersion httpVersion;
    private StatusCode statusCode;

    public StatusLine(StatusCode statusCode) {
        this(HttpVersion.HTTP_1_1, statusCode);
    }

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
