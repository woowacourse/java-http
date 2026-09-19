package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {

    private final String ALLOWED_METHOD = "GET";
    private final String ALLOWED_VERSION = "HTTP/1.1";
    private static final String ROOT_PATH = "/";
    private static final String STATIC_TARGET_PATH = "static";

    private final String method;
    private final String path;
    private final Map<String, String> queryParameters;
    private final String version;

    private HttpRequest(String method, String path, Map<String, String> queryParameters, String version) {
        validate(method, version);
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.version = version;
    }

    public static HttpRequest from(String startLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(startLine, " ");
        String method = stringTokenizer.nextToken();
        String requestUri = stringTokenizer.nextToken();
        String version = stringTokenizer.nextToken();

        int index = requestUri.indexOf("?");
        if (index == -1) {
            return new HttpRequest(method, requestUri, Map.of(), version);
        }
        String path = requestUri.substring(0, index) + ".html";
        Map<String, String> queryParameters = parseQueryParameters(requestUri.substring(index + 1));
        return new HttpRequest(method, path, queryParameters, version);
    }

    public boolean isRoot() {
        return ROOT_PATH.equals(path);
    }

    public boolean isParameterEmpty() {
        return queryParameters.isEmpty();
    }

    public String getParameter(String key) {
        return queryParameters.get(key);
    }

    public String getResourcePath() {
        return STATIC_TARGET_PATH + path;
    }

    private static Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(queryString, "&");

        while (stringTokenizer.hasMoreTokens()) {
            String parameter = stringTokenizer.nextToken();
            int index = parameter.indexOf("=");
            String key = parameter.substring(0, index);
            String value = parameter.substring(index + 1);
            queryParameters.put(key, value);
        }
        return queryParameters;
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
