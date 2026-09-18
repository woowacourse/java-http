package org.apache.coyote.http11.request;

import java.util.Set;
import org.apache.coyote.http11.BadRequestException;

public class HttpRequest {

    private static final String SUPPORTED_VERSION = "HTTP/1.1";

    private final String method;
    private final HttpRequestTarget target;
    private final String version;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(
            String method,
            HttpRequestTarget target,
            String version,
            HttpHeaders headers,
            HttpBody body
    ) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(String requestLine) {
        return from(requestLine, HttpHeaders.empty(), HttpBody.empty());
    }

    public static HttpRequest from(String requestLine, Set<String> supportedMethods) {
        return from(requestLine, HttpHeaders.empty(), HttpBody.empty(), supportedMethods);
    }

    public static HttpRequest from(String requestLine, HttpHeaders headers, HttpBody body) {
        String[] requestParts = parseRequestLine(requestLine);
        validateVersion(requestParts[2]);

        return new HttpRequest(
                requestParts[0],
                new HttpRequestTarget(requestParts[1]),
                requestParts[2],
                headers,
                body
        );
    }

    public static HttpRequest from(
            String requestLine,
            HttpHeaders headers,
            HttpBody body,
            Set<String> supportedMethods
    ) {
        HttpRequest request = from(requestLine, headers, body);
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

    public String getHeader(String name) {
        return headers.get(name);
    }

    public HttpBody getBody() {
        return body;
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
