package org.apache.coyote.http;

public class RequestLine {

    private final RequestMethod method;
    private final String path;
    private final String version;

    private RequestLine(String method, String path, String version) {
        this.method = RequestMethod.fromString(method);
        this.path = path;
        this.version = version;
    }

    public static RequestLine from(String requestLine) {
        String[] parts = splitRequestLine(requestLine);
        String method = parts[0];
        String path = parts[1];
        String version = parts[2];
        return new RequestLine(method, path, version);
    }

    private static String[] splitRequestLine(String requestLine) {
        return requestLine.split(" ");
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
