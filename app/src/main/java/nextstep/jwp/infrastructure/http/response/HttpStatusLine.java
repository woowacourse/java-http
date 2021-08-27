package nextstep.jwp.infrastructure.http.response;

public class HttpStatusLine {

    private final String httpVersion;
    private final HttpStatusCode statusCode;

    public HttpStatusLine(final String httpVersion, final HttpStatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return String.join(" ", httpVersion, statusCode.name(), String.valueOf(statusCode.value()));
    }
}
