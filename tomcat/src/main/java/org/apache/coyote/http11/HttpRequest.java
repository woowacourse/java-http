package org.apache.coyote.http11;

import java.util.Set;

public class HttpRequest {

    private static final String SUPPORTED_VERSION = "HTTP/1.1";

    private final String method;
    private final HttpRequestTarget target;
    private final String version;

    public HttpRequest(String method, HttpRequestTarget target, String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static HttpRequest from(String requestLine) {
        String[] requestParts = parseRequestLine(requestLine);
        validateVersion(requestParts[2]);

        return new HttpRequest(requestParts[0], new HttpRequestTarget(requestParts[1]), requestParts[2]);
    }

    public static HttpRequest from(String requestLine, Set<String> supportedMethods) {
        HttpRequest request = from(requestLine);
        request.validateMethod(supportedMethods);

        return request;
    }

    public String getMethod() {
        return method;
    }

    public String getHttpPath() {
        return target.getPath();
    }

    public String getParams(String key) {
        return target.getParams(key);
    }

    public String getVersion() {
        return version;
    }

    private static String[] parseRequestLine(String requestLine) {
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length != 3) {
            throw new BadRequestException("잘못된 http요청 형태입니다.");
        }

        return requestParts;
    }

    private static void validateVersion(String version) {
        if (!SUPPORTED_VERSION.equals(version)) {
            throw new BadRequestException("지원하지 않는 HTTP 버전입니다: " + version);
        }
    }

    private void validateMethod(Set<String> supportedMethods) {
        if (!supportedMethods.contains(method)) {
            throw new BadRequestException("지원하지 않는 HTTP 메서드입니다: " + method);
        }
    }
}
