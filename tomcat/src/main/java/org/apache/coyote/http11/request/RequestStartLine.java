package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;

public class RequestStartLine {
    private static final String AVAILABLE_VERSION = "HTTP/1.1";

    private final HttpMethod method;
    private final HttpPath path;

    public RequestStartLine(String method, String path, String protocolVersion) {
        validateVersion(protocolVersion);
        this.method = HttpMethod.from(method);
        this.path = new HttpPath(path);
    }

    private void validateVersion(String version) {
        if (AVAILABLE_VERSION.equals(version)) {
            return;
        }
        throw new UncheckedServletException("해당 버전의 프로토콜을 지원하지 않습니다.");
    }

    public String getPath() {
        return path.getPath();
    }
}
