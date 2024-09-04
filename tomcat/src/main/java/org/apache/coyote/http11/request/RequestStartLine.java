package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Objects;
import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        RequestStartLine that = (RequestStartLine) object;
        return method == that.method && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RequestStartLine.class.getSimpleName() + "[", "]")
                .add("method=" + method)
                .add("path=" + path)
                .toString();
    }
}
