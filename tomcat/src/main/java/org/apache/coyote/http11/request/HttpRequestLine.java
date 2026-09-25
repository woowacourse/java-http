package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;

public class HttpRequestLine {

    private static final Set<String> ALLOWED_METHODS = Set.of("GET", "POST");
    private static final String ALLOWED_VERSION = "HTTP/1.1";

    private final String method;
    private final String path;
    private final HttpParameters queryParameters;
    private final String version;

    private HttpRequestLine(String method, String path, HttpParameters queryParameters, String version) {
        validate(method, version);
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.version = version;
    }

    public static HttpRequestLine from(String requestLine) throws IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(requestLine, " ");
        String method = stringTokenizer.nextToken();
        String requestUri = stringTokenizer.nextToken();
        String version = stringTokenizer.nextToken();

        int index = requestUri.indexOf("?");
        if (index == -1) {
            return new HttpRequestLine(method, requestUri, HttpParameters.empty(), version);
        }

        String path = requestUri.substring(0, index);

        HttpParameters queryParameters = HttpParameters.from(requestUri.substring(index + 1));

        return new HttpRequestLine(method, path, queryParameters, version);
    }

    public boolean isMatchedMethod(String otherMethod) {
        return method.equals(otherMethod);
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    private void validate(String method, String version) {
        validateMethod(method);
        validateVersion(version);
    }

    private void validateMethod(String method) {
        if (ALLOWED_METHODS.contains(method)) {
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
