package org.apache.coyote.http11;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.exception.HttpParseException;

import java.util.Objects;

public class RequestLine {
    private static final String SEPARATOR = " ";
    private static final int PART_COUNT = 3;
    private static final String SUPPORTED_VERSION = "HTTP/1.1";

    private final HttpMethod method;
    private final RequestUri uri;
    private final String version;

    private RequestLine(HttpMethod method, RequestUri uri, String version) {
        validateVersion(version);
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    private void validateVersion(String version) {
        if (!SUPPORTED_VERSION.equals(version)) {
            throw new HttpParseException("지원하지 않는 HTTP 버전입니다: " + version);
        }
    }

    public static RequestLine from(String line) {
        String[] parts = line.strip().split(SEPARATOR);
        if (parts.length != PART_COUNT) {
            throw new HttpParseException("요청 라인은 'method uri version' 형식이어야 합니다: " + line);
        }
        return new RequestLine(HttpMethod.from(parts[0]), new RequestUri(parts[1]), parts[2]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri, version);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RequestLine that = (RequestLine) o;
        return method == that.method && Objects.equals(uri, that.uri) && Objects.equals(version, that.version);
    }

    public RequestUri getUri() {
        return uri;
    }
}
