package nextstep.jwp.infrastructure.http.response;

import java.util.Objects;

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
        return String.join(" ", httpVersion, String.valueOf(statusCode.value()), statusCode.getMessage(), "");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpStatusLine that = (HttpStatusLine) o;
        return Objects.equals(httpVersion, that.httpVersion) && statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpVersion, statusCode);
    }
}
