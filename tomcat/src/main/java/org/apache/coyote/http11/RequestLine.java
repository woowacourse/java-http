package org.apache.coyote.http11;

import java.util.Optional;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final int PARTS_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final String method;
    private final RequestUri requestUri;
    private final String version;

    private RequestLine(final String method, final RequestUri requestUri, final String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public static RequestLine from(final String line) {
        if (line == null || line.isBlank()) {
            throw new InvalidRequestException("Request Line이 비어 있습니다.");
        }

        final String[] parts = line.split(DELIMITER);
        if (parts.length != PARTS_COUNT) {
            throw new InvalidRequestException("잘못된 형식의 Request Line: " + line);
        }

        return new RequestLine(
                parts[METHOD_INDEX],
                RequestUri.from(parts[URI_INDEX]),
                parts[VERSION_INDEX]
        );
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public boolean hasQueryParameters() {
        return requestUri.hasQueryParameters();
    }

    public Optional<String> getQueryParameter(final String name) {
        return requestUri.getQueryParameter(name);
    }

    public String getVersion() {
        return version;
    }
}
