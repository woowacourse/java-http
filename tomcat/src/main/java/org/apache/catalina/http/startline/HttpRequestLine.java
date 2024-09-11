package org.apache.catalina.http.startline;

import java.nio.file.Path;
import org.apache.catalina.exception.CatalinaException;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, RequestURI requestURI, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine parse(String startLine) {
        String[] separatedStartLine = validateAndSplit(startLine);
        return new HttpRequestLine(
                HttpMethod.valueOf(separatedStartLine[0]),
                new RequestURI(separatedStartLine[1]),
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

    public boolean isURIStatic() {
        return httpMethod.equals(HttpMethod.GET) && requestURI.isResource();
    }

    public boolean isURIBlank() {
        return requestURI.isBlank();
    }

    public boolean URIStartsWith(String startsWith) {
        return requestURI.startsWith(startsWith);
    }

    public Path getTargetPath() {
        return requestURI.getPath();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
