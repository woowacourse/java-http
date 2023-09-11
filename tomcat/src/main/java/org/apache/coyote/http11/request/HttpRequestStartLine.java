package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequestStartLine {
    private final HttpMethod method;
    private final String path;

    private HttpRequestStartLine(final HttpMethod method, final String path) {
        this.method = method;
        this.path = path;
    }

    public static HttpRequestStartLine from(final String startLine) {
        if (startLine == null) {
            throw new IllegalArgumentException("HTTP 요청이 올바르게 입력되지 않았습니다.");
        }
        final List<String> startLineTokens = List.of(startLine.split(" "));
        final HttpMethod method = HttpMethod.valueOf(startLineTokens.get(0));
        return makeStartLine(startLineTokens.get(1), method);
    }

    private static HttpRequestStartLine makeStartLine(String uri, HttpMethod method) {
        int uriSeparatorIndex = uri.indexOf("?");
        if (uriSeparatorIndex == -1) {
            return new HttpRequestStartLine(method, uri);
        }
        return new HttpRequestStartLine(method, uri.substring(0, uriSeparatorIndex));
    }

    public boolean isPOST() {
        return this.method.equals(HttpMethod.POST);
    }

    public boolean isGET() {
        return this.method.equals(HttpMethod.GET);
    }

    public boolean isSamePath(String path) {
        return this.path.equals(path);
    }

    public String getPath() {
        return this.path;
    }
}
