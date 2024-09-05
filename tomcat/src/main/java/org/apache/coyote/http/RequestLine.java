package org.apache.coyote.http;

import java.util.List;

public class RequestLine {

    private final HttpMethod method;
    private final Path path;
    private final HttpVersion version;

    public static RequestLine of(String requestLine) {
        List<String> requestLines = List.of(requestLine.split(" "));
        if (requestLines.size() != 3) {
            throw new IllegalArgumentException("Invalid request line: " + requestLines);
        }
        String originMethod = requestLines.get(0);
        String originPath = requestLines.get(1);
        String originVersion = requestLines.get(2);
        return new RequestLine(HttpMethod.findByMethod(originMethod), new Path(originPath), HttpVersion.findByVersion(originVersion));
    }

    private RequestLine(HttpMethod method, Path path, HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Path getPath() {
        return path;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
