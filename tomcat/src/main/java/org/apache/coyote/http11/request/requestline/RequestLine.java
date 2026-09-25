package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.exception.BadRequestException;

import java.util.Objects;
import java.util.Optional;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int KEEP_TRAILING_EMPTY = -1;
    private static final int PARTS_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final HttpVersion version;

    public RequestLine(final HttpMethod method, final RequestUri requestUri, final HttpVersion version) {
        this.method = Objects.requireNonNull(method);
        this.requestUri = Objects.requireNonNull(requestUri);
        this.version = Objects.requireNonNull(version);
    }

    public static RequestLine from(final String line) {
        if (line == null || line.isBlank()) {
            throw new BadRequestException("Request Line이 비어 있습니다.");
        }

        final String[] parts = line.split(DELIMITER, KEEP_TRAILING_EMPTY);
        if (parts.length != PARTS_COUNT) {
            throw new BadRequestException("요청 라인 형식이 잘못되었습니다");
        }
        return new RequestLine(
                HttpMethod.from(parts[METHOD_INDEX]),
                RequestUri.from(parts[URI_INDEX]),
                HttpVersion.from(parts[VERSION_INDEX])
        );
    }

    public HttpMethod getMethod() {
        return method;
    }

    public boolean isMethod(final HttpMethod expected) {
        return method == expected;
    }

    public RequestPath getPath() {
        return requestUri.getRequestPath();
    }

    public Optional<String> getQueryParameter(final String name) {
        return requestUri.getQueryParameter(name);
    }

    public HttpVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return method + " " + requestUri.getRequestPath() + " " + version.getValue();
    }
}
