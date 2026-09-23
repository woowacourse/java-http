package org.apache.coyote.http11;

import java.net.URI;

class RequestLine {

    private static final int REQUEST_LINE_PARTS_SIZE = 3;

    private final HttpMethod method;
    private final String path;
    private final String version;

    RequestLine(String line) {
        String[] parts = line.trim().split("\\s+");
        validate(parts, line);

        this.method = HttpMethod.from(parts[0]);
        this.path = parsePath(parts[1]);
        this.version = parts[2];
    }

    private String parsePath(String target) {
        URI uri = URI.create(target);
        if (!target.startsWith("/") || uri.getRawAuthority() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("Invalid request target");
        }
        return uri.getPath();
    }

    private void validate(String[] parts, String line) {
        if (parts.length != REQUEST_LINE_PARTS_SIZE) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + line);
        }
    }

    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getVersion() {
        return version;
    }
}
