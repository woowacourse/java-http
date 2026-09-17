package org.apache.coyote.http11;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.exception.HttpParseException;

import java.util.Objects;

public class RequestLine {
    private static final String SEPARATOR = " ";
    private static final int PART_COUNT = 3;
    private static final String URI_PREFIX = "/";
    private static final String SUPPORTED_VERSION = "HTTP/1.1";

    private final HttpMethod method;
    private final String uri;
    private final String version;

    private RequestLine(HttpMethod method, String uri, String version) {
        validateUri(uri);
        validateVersion(version);
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine from(String line) {
        String[] parts = line.strip().split(SEPARATOR);
        if (parts.length != PART_COUNT) {
            throw new HttpParseException("요청 라인은 'method uri version' 형식이어야 합니다: " + line);
        }
        return new RequestLine(HttpMethod.from(parts[0]), parts[1], parts[2]);
    }

    private void validateUri(String uri) {
        if (!uri.startsWith(URI_PREFIX)) {
            throw new HttpParseException("요청 경로는 '/'로 시작해야 합니다: " + uri);
        }
    }

    private void validateVersion(String version) {
        if (!SUPPORTED_VERSION.equals(version)) {
            throw new HttpParseException("지원하지 않는 HTTP 버전입니다: " + version);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLine that)) {
            return false;
        }
        return method == that.method && uri.equals(that.uri) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri, version);
    }
}
