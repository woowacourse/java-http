package org.apache.coyote.http11;

import java.util.StringTokenizer;

public class HttpRequest {

    private final String ALLOWED_METHOD = "GET";
    private final String ALLOWED_VERSION = "HTTP/1.1";

    private final String method;
    private final HttpRequestUri httpRequestUri;
    private final String version;

    private HttpRequest(String method, HttpRequestUri httpRequestUri, String version) {
        validate(method, version);
        this.method = method;
        this.httpRequestUri = httpRequestUri;
        this.version = version;
    }

    public static HttpRequest from(String startLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(startLine, " ");
        String method = stringTokenizer.nextToken();
        HttpRequestUri httpRequestUri = HttpRequestUri.from(stringTokenizer.nextToken());
        String version = stringTokenizer.nextToken();
        return new HttpRequest(method, httpRequestUri, version);
    }

    public boolean isRoot() {
        return httpRequestUri.isRoot();
    }

    public boolean isParameterEmpty() {
        return httpRequestUri.isParameterEmpty();
    }

    public String getParameter(String key) {
        return httpRequestUri.getParameter(key);
    }

    public String getResourcePath() {
        return httpRequestUri.getResourcePath();
    }

    private void validate(String method, String version) {
        validateMethod(method);
        validateVersion(version);
    }

    private void validateMethod(String method) {
        if (ALLOWED_METHOD.equals(method)) {
            return;
        }
        throw new IllegalArgumentException("400 지원하지 않는 HTTP 메서드입니다: " + method);
    }

    private void validateVersion(String version) {
        if (ALLOWED_VERSION.equals(version)) {
            return;
        }
        throw new IllegalArgumentException("400 지원하지 않는 HTTP 버전입니다: " + version);
    }
}
