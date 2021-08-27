package nextstep.jwp.infrastructure.http.response;

public class HttpStatusLine {

    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private final HttpStatusCode statusCode;

    public HttpStatusLine(final String httpVersion, final HttpStatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public HttpStatusLine(final HttpStatusCode statusCode) {
        this(DEFAULT_HTTP_VERSION, statusCode);
    }

    @Override
    public String toString() {
        return String.join(" ", httpVersion, String.valueOf(statusCode.value()), statusCode.name(), "");
    }
}
