package org.apache.coyote.http11;

import java.util.StringTokenizer;

public class HttpRequest {

    private final String ALLOWED_METHOD = "GET";
    private final String ALLOWED_VERSION = "HTTP/1.1";
    private static final String ROOT_PATH = "/";
    private static final String STATIC_TARGET_PATH = "static";

    private final String method;
    private final String target;
    private final String version;

    private HttpRequest(String method, String target, String version) {
        validate(method, version);
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static HttpRequest from(String startLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(startLine, " ");
        String method = stringTokenizer.nextToken();
        String target = stringTokenizer.nextToken();
        String version = stringTokenizer.nextToken();
        return new HttpRequest(method, target, version);
    }

    public boolean isRoot() {
        if (ROOT_PATH.equals(target)) {
            return true;
        }
        return false;
    }

    public String findTargetPath() {
        return STATIC_TARGET_PATH + target;
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

    public String getTarget() {
        return target;
    }
}
