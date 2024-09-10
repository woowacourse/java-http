package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpVersion;

import java.util.List;

public class RequestLine {

    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final int REQUEST_LINE_PARAMETERS_COUNT = 3;

    private final HttpMethod method;
    private final Path path;
    private final HttpVersion version;

    public static RequestLine of(String requestLine) {
        List<String> requestLines = List.of(requestLine.split(REQUEST_LINE_SEPARATOR));
        if (requestLines.size() != REQUEST_LINE_PARAMETERS_COUNT) {
            throw new IllegalArgumentException("Invalid request line: " + requestLines);
        }
        String originMethod = requestLines.get(METHOD_INDEX);
        String originPath = requestLines.get(PATH_INDEX);
        String originVersion = requestLines.get(VERSION_INDEX);
        return new RequestLine(
                HttpMethod.findByMethod(originMethod),
                new Path(originPath),
                HttpVersion.findByVersion(originVersion));
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
