package org.apache.coyote.http11.request.requestLine;

import org.apache.coyote.http11.request.HttpVersion;

public class HttpRequestLine {

    private final static String REQUEST_LINE_SEPARATOR = " ";

    private final MethodType methodType;
    private Path path;
    private final HttpVersion httpVersion;

    private HttpRequestLine(MethodType methodType, Path path, HttpVersion httpVersion) {
        this.methodType = methodType;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine toHttpRequestLine(String requestLine) {
        String[] splitLines = requestLine.split(REQUEST_LINE_SEPARATOR);
        validateRequestLineForm(splitLines);
        MethodType methodType = MethodType.toMethodType(splitLines[0]);
        Path path = new Path(splitLines[1]);
        HttpVersion httpVersion = HttpVersion.toHttpVersion(splitLines[2]);
        return new HttpRequestLine(methodType, path, httpVersion);
    }

    private static void validateRequestLineForm(String[] splitLines) {
        if (splitLines.length != 3) {
            throw new IllegalArgumentException("잘못된 RequestLine 형식입니다.");
        }
    }

    public String getPath() {
        return path.getValue();
    }

    public MethodType getMethodType() {
        return methodType;
    }
}
