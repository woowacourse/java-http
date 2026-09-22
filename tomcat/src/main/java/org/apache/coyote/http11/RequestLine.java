package org.apache.coyote.http11;

class RequestLine {

    private static final int REQUEST_LINE_PARTS_SIZE = 3;

    private final HttpMethod method;
    private final String path;
    private final String version;

    RequestLine(String line) {
        String[] parts = line.trim().split("\\s+", REQUEST_LINE_PARTS_SIZE);
        validate(parts, line);

        this.method = HttpMethod.from(parts[0]);
        this.path = parts[1];
        this.version = parts[2];
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
