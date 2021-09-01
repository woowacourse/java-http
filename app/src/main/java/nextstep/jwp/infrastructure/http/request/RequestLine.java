package nextstep.jwp.infrastructure.http.request;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RequestLine {

    private static final String BLANK = " ";
    private static final int FIRST_LINE_ELEMENT_SIZE = 3;
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final Method method;
    private final URI uri;
    private final String httpVersion;

    public RequestLine(final Method method, final String uri, final String httpVersion) {
        this.method = method;
        this.uri = URI.of(uri);
        this.httpVersion = httpVersion;
    }

    public RequestLine(final Method method, final String uri) {
        this(method, uri, DEFAULT_HTTP_VERSION);
    }

    public static RequestLine of(String line) {
        final List<String> result = Arrays.asList(line.split(BLANK));

        if (result.size() != FIRST_LINE_ELEMENT_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid HttpRequest Format.(%s)", line));
        }

        return new RequestLine(Method.valueOf(result.get(METHOD_INDEX)), result.get(URI_INDEX), result.get(VERSION_INDEX));
    }

    public String getBaseUri() {
        return uri.getBaseUri();
    }

    public Method getMethod() {
        return method;
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
        final RequestLine that = (RequestLine) o;
        return method == that.method && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri);
    }
}
