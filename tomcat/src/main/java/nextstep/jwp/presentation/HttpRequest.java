package nextstep.jwp.presentation;

import java.util.Objects;

public class HttpRequest {

    private final String uri;
    private final String method;

    public HttpRequest(final String uri, final String method) {
        this.uri = uri;
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(getUri(), that.getUri()) && Objects.equals(getMethod(), that.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri(), getMethod());
    }
}
