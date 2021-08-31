package nextstep.jwp.infrastructure.http.request;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HttpRequestLine {

    private static final String BLANK = " ";
    private static final int FIRST_LINE_ELEMENT_SIZE = 3;
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final URI uri;
    private final String httpVersion;

    public HttpRequestLine(final HttpMethod httpMethod, final String uri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = URI.of(uri);
        this.httpVersion = httpVersion;
    }

    public HttpRequestLine(final HttpMethod httpMethod, final String uri) {
        this(httpMethod, uri, DEFAULT_HTTP_VERSION);
    }

    public static HttpRequestLine of(String line) {
        final List<String> result = Arrays.asList(line.split(BLANK));

        if (result.size() != FIRST_LINE_ELEMENT_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid HttpRequest Format.(%s)", line));
        }

        return new HttpRequestLine(HttpMethod.valueOf(result.get(METHOD_INDEX)), result.get(URI_INDEX), result.get(VERSION_INDEX));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequestLine that = (HttpRequestLine) o;
        return httpMethod == that.httpMethod && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri);
    }
}
