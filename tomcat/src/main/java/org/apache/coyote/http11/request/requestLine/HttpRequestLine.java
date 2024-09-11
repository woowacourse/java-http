package org.apache.coyote.http11.request.requestLine;

public class HttpRequestLine {

    private final static String REQUEST_LINE_SEPARATOR = " ";

    private final MethodType methodType;
    private final Path path;
    private final HttpVersion httpVersion;

    private HttpRequestLine(MethodType methodType, Path path, HttpVersion httpVersion) {
        this.methodType = methodType;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine toHttpRequestLine(String requestLine) {
        String[] splitLines = requestLine.split(REQUEST_LINE_SEPARATOR);
        MethodType methodType = MethodType.toMethodType(splitLines[0]);
        Path path = new Path(splitLines[1]);
        HttpVersion httpVersion = HttpVersion.toHttpVersion(splitLines[2]);
        return new HttpRequestLine(methodType, path, httpVersion);
    }

    public String getPath() {
        return path.getValue();
    }

    public MethodType getMethodType() {
        return methodType;
    }
}
