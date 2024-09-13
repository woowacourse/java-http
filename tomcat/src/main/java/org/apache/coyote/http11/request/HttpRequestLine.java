package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpRequestLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestLine from(String requestLine) {
        String[] parsedStartLine = parseRequestLine(requestLine);
        HttpMethod method = HttpMethod.from(parsedStartLine[0]);
        String path = parsedStartLine[1];
        String version = parsedStartLine[2];
        return new HttpRequestLine(method, path, version);
    }

    private static String[] parseRequestLine(String requestLine) {
        String[] parsedRequestLine = requestLine.split(" ");
        if (parsedRequestLine.length != 3) {
            throw new IllegalArgumentException("Invalid request line: " + requestLine);
        }
        return parsedRequestLine;
    }

    public boolean isGet() {
        return method == HttpMethod.GET;
    }

    public boolean isPost() {
        return method == HttpMethod.POST;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
