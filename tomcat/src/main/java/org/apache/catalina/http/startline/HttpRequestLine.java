package org.apache.catalina.http.startline;

import java.nio.file.Path;
import org.apache.catalina.exception.CatalinaException;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, RequestUri requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine parse(String startLine) {
        String[] separatedStartLine = validateAndSplit(startLine);
        return new HttpRequestLine(
                HttpMethod.valueOf(separatedStartLine[0]),
                new RequestUri(separatedStartLine[1]),
                HttpVersion.parse(separatedStartLine[2])
        );
    }

    private static String[] validateAndSplit(String startLine) {
        String[] values = startLine.split(" ");
        if (values.length == 2) {
            return new String[]{values[0], "", values[1]};
        }
        if (values.length == 3) {
            return values;
        }
        throw new CatalinaException("Invalid start line: " + startLine);
    }

    public boolean isTargetStatic() {
        return httpMethod.equals(HttpMethod.GET) && requestUri.isResource();
    }

    public boolean isTargetBlank() {
        return requestUri.isBlank();
    }

    public boolean uriStartsWith(String startsWith) {
        return requestUri.startsWith(startsWith);
    }

    public Path getTargetPath() {
        return requestUri.getPath();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
