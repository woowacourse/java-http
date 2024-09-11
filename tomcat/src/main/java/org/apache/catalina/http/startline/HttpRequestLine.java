package org.apache.catalina.http.startline;

import org.apache.catalina.exception.CatalinaException;

public class HttpRequestLine {

    private static final String SEPARATOR = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int REQUEST_LINE_ELEMENT_COUNT = 3;

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, RequestURI requestURI, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine parse(String startLine) {
        String[] separatedStartLine = split(startLine);
        return new HttpRequestLine(
                HttpMethod.valueOf(separatedStartLine[HTTP_METHOD_INDEX]),
                new RequestURI(separatedStartLine[REQUEST_URI_INDEX]),
                HttpVersion.parse(separatedStartLine[HTTP_VERSION_INDEX])
        );
    }

    private static String[] split(String startLine) {
        String[] elements = startLine.split(SEPARATOR);
        if (elements.length == REQUEST_LINE_ELEMENT_COUNT) {
            return elements;
        }
        throw new CatalinaException("Invalid start line: " + startLine);
    }

    public boolean isURIStatic() {
        return httpMethod.equals(HttpMethod.GET) && requestURI.isResource();
    }

    public boolean isURIHome() {
        return requestURI.isHome();
    }

    public boolean URIStartsWith(String startsWith) {
        return requestURI.startsWith(startsWith);
    }

    public String getURI() {
        return requestURI.getValue();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
