package org.apache.coyote.http11.request;

import java.util.Set;
import org.apache.coyote.http11.BadRequestException;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final String SUPPORTED_VERSION = "HTTP/1.1";

    private final String method;
    private final HttpRequestTarget target;
    private final String version;

    public RequestLine(String method, HttpRequestTarget target, String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static RequestLine from(String requestLine) {
        String[] requestParts = parse(requestLine);
        validateVersion(requestParts[2]);

        return new RequestLine(
                requestParts[0],
                new HttpRequestTarget(requestParts[1]),
                requestParts[2]
        );
    }

    public void validateMethod(Set<String> supportedMethods) {
        if (!supportedMethods.contains(method)) {
            throw new BadRequestException("지원하지 않는 HTTP 메서드입니다: " + method);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return target.getPath();
    }

    public String getParams(String key) {
        return target.getParams(key);
    }

    public String getVersion() {
        return version;
    }

    private static String[] parse(String requestLine) {
        String[] requestParts = requestLine.split(DELIMITER);

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
}
