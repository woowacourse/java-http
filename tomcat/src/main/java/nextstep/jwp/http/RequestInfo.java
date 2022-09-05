package nextstep.jwp.http;

import org.apache.http.HttpMethod;

import java.util.Objects;

public class RequestInfo {

    private final HttpMethod httpMethod;
    private final String uri;

    public RequestInfo(final HttpMethod httpMethod, final String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public static RequestInfo of(final RequestEntity requestEntity) {
        return new RequestInfo(requestEntity.getHttpMethod(), requestEntity.getUri());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestInfo)) return false;
        final RequestInfo that = (RequestInfo) o;
        return httpMethod == that.httpMethod && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return this.uri;
    }
}
